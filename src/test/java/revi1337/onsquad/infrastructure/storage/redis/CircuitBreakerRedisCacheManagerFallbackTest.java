package revi1337.onsquad.infrastructure.storage.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew_member.application.leaderboard.CrewRankerCacheService;
import revi1337.onsquad.crew_member.application.response.CrewRankerResponse;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;

@Import(CircuitBreakerRedisCacheManagerFallbackTest.BrokenRedisCacheManagerConfig.class)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class CircuitBreakerRedisCacheManagerFallbackTest extends ApplicationLayerTestSupport {

    private static final String CIRCUIT_BREAKER_NAME = "redisCacheCircuitBreaker";

    @SpyBean
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private CrewRankerCacheService crewRankerCacheService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).reset();
    }

    @Test
    @DisplayName("Redis 연결이 불가능해도 @Cacheable 조회는 예외 없이 원본 메서드(DB)로 우회된다")
    void fallsBackToOriginalMethod_whenRedisIsUnreachable() {
        Long crewId = 1L;
        Member revi = createRevi(1L);
        crewRankerRepository.insertBatch(List.of(new CrewRankerCandidate(
                crewId, 1, 1, revi.getId(), revi.getNickname().getValue(), revi.getMbti().name(), LocalDateTime.now()
        )));

        List<CrewRankerResponse> first = crewRankerCacheService.findAllByCrewId(crewId);
        List<CrewRankerResponse> second = crewRankerCacheService.findAllByCrewId(crewId);

        assertThat(first).hasSize(1);
        assertThat(second).hasSize(1);
        verify(crewRankerRepository, times(2)).findAllByCrewId(crewId);
    }

    @Test
    @DisplayName("Redis 장애가 누적되면 서킷이 OPEN 되어 이후 호출은 Redis 를 시도하지 않는다")
    void opensCircuit_whenRedisFailuresAccumulate() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);
        Long crewId = 1L;

        for (int i = 0; i < 150; i++) {
            crewRankerCacheService.findAllByCrewId(crewId);
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @TestConfiguration
    static class BrokenRedisCacheManagerConfig {

        /**
         * 운영 {@code RedisCacheManagerConfig.redisCacheManager()} 빈을, 아무도 수신 대기하지 않는 포트로 바라보도록 덮어써서 Redis 연결 실패를 강제로 유발한다.
         */
        @Bean
        public CircuitBreakerRedisCacheManager redisCacheManager(CircuitBreakerRegistry circuitBreakerRegistry) {
            RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration("127.0.0.1", 54321);
            LettuceConnectionFactory brokenConnectionFactory = new LettuceConnectionFactory(standaloneConfiguration);
            brokenConnectionFactory.afterPropertiesSet();
            return new CircuitBreakerRedisCacheManager(
                    RedisCacheWriter.nonLockingRedisCacheWriter(brokenConnectionFactory),
                    RedisCacheConfiguration.defaultCacheConfig(),
                    Map.of(),
                    circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME)
            );
        }
    }
}
