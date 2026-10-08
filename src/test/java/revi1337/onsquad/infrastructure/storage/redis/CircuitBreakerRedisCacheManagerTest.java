package revi1337.onsquad.infrastructure.storage.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.announce.application.AnnounceCacheEvictor;
import revi1337.onsquad.announce.application.AnnounceCacheEvictorFactory;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.application.dto.response.AnnounceResponse;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceQueryDslRepository;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.announce.infrastructure.RedisAnnounceCacheEvictor;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_member.application.leaderboard.CrewRankerCacheService;
import revi1337.onsquad.crew_member.application.response.CrewRankerResponse;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class CircuitBreakerRedisCacheManagerTest extends ApplicationLayerTestSupport {

    private static final String CIRCUIT_BREAKER_NAME = "redisCacheCircuitBreaker";

    @Autowired
    @Qualifier("redisCacheManager")
    private CacheManager redisCacheManager;

    @Autowired
    private AnnounceCacheEvictorFactory announceCacheEvictorFactory;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @SpyBean
    private CrewRankerRepository crewRankerRepository;

    @SpyBean
    private AnnounceQueryDslRepository announceQueryDslRepository;

    @Autowired
    private CrewRankerCacheService crewRankerCacheService;

    @Autowired
    private AnnounceCacheService announceCacheService;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
        circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);
        circuitBreaker.reset();
    }

    @Nested
    @DisplayName("빈 구성")
    class BeanWiring {

        @Test
        @DisplayName("redisCacheManager 는 RedisCacheManager 의 서브클래스이며 반환하는 Cache 는 CircuitBreakerCache 이다")
        void redisCacheManagerIsRedisCacheManagerWithCircuitBreakerCache() {
            assertThat(redisCacheManager).isInstanceOf(RedisCacheManager.class);
            assertThat(redisCacheManager.getCache(CacheConst.CREW_ANNOUNCE)).isInstanceOf(CircuitBreakerCache.class);
            assertThat(redisCacheManager.getCache("runtime-created-cache")).isInstanceOf(CircuitBreakerCache.class);
        }

        @Test
        @DisplayName("RedisAnnounceCacheEvictor 가 redisCacheManager 를 지원한다")
        void redisAnnounceCacheEvictorSupportsRedisCacheManager() {
            AnnounceCacheEvictor evictor = announceCacheEvictorFactory.findAvailableEvictor(redisCacheManager);

            assertThat(evictor).isExactlyInstanceOf(RedisAnnounceCacheEvictor.class);
        }
    }

    @Nested
    @DisplayName("서킷 CLOSED 상태")
    class Closed {

        @Test
        @DisplayName("Cache put 한 값을 get 하면 그대로 조회된다")
        void putAndGet() {
            Cache cache = redisCacheManager.getCache(CacheConst.CREW_ANNOUNCE);

            cache.put("key", "value");

            assertThat(cache.get("key", String.class)).isEqualTo("value");
            cache.evict("key");
            assertThat(cache.get("key")).isNull();
        }

        @Test
        @DisplayName("@Cacheable 은 반복 호출 시 DB 를 한 번만 조회한다")
        void cacheableHits() {
            Long crewId = 1L;
            crewRankerRepository.insertBatch(List.of(createCandidate(crewId, createRevi(1L))));

            crewRankerCacheService.findAllByCrewId(crewId);
            crewRankerCacheService.findAllByCrewId(crewId);

            verify(crewRankerRepository, times(1)).findAllByCrewId(crewId);
        }
    }

    @Nested
    @DisplayName("서킷 OPEN 상태")
    class Open {

        @BeforeEach
        void openCircuit() {
            circuitBreaker.transitionToOpenState();
        }

        @Test
        @DisplayName("@Cacheable 조회는 miss 로 처리되어 매번 원본 메서드가 실행되고 정상 결과를 반환한다")
        void cacheableFallsBackToOriginalMethod() {
            Long crewId = 1L;
            crewRankerRepository.insertBatch(List.of(createCandidate(crewId, createRevi(1L)), createCandidate(crewId, createAndong(2L))));

            List<CrewRankerResponse> first = crewRankerCacheService.findAllByCrewId(crewId);
            List<CrewRankerResponse> second = crewRankerCacheService.findAllByCrewId(crewId);

            assertThat(first).hasSize(2);
            assertThat(second).hasSize(2);
            verify(crewRankerRepository, times(2)).findAllByCrewId(crewId);
        }

        @Test
        @DisplayName("@CachePut 의 put 실패는 삼켜지고 메서드 본문의 결과가 정상 반환된다")
        void cachePutFailureIsSwallowed() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createAnnounce(crew, revi));

            AnnounceResponse response = announceCacheService.putAnnounce(crew.getId(), announce.getId());
            List<AnnounceResponse> responses = announceCacheService.putDefaultAnnounceList(crew.getId());

            assertThat(response).isNotNull();
            assertThat(responses).hasSize(1);
            verify(announceQueryDslRepository, times(1)).fetchByIdAndCrewId(announce.getId(), crew.getId());
        }

        @Test
        @DisplayName("서킷이 CLOSED 로 복구되면 다시 캐시가 동작한다")
        void cacheWorksAgain_afterCircuitRecovered() {
            Long crewId = 1L;
            crewRankerRepository.insertBatch(List.of(createCandidate(crewId, createRevi(1L))));
            crewRankerCacheService.findAllByCrewId(crewId);

            circuitBreaker.transitionToClosedState();
            crewRankerCacheService.findAllByCrewId(crewId);
            crewRankerCacheService.findAllByCrewId(crewId);

            verify(crewRankerRepository, times(2)).findAllByCrewId(crewId);
        }
    }

    private CrewRankerCandidate createCandidate(Long crewId, Member member) {
        return new CrewRankerCandidate(
                crewId,
                1,
                1,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                LocalDateTime.now()
        );
    }

    private Announce createAnnounce(Crew crew, Member writer) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, writer);
    }
}
