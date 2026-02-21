package revi1337.onsquad.announce.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.announce.application.dto.response.AnnounceResponse;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceQueryDslRepository;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class AnnounceCacheServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @SpyBean
    private AnnounceQueryDslRepository announceQueryDslRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AnnounceCacheService announceCacheService;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("공지사항 단건 조회 시 첫 호출만 DB에 접근하고 이후에는 캐시에서 반환한다.")
    void getAnnounce() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Announce announce = announceRepository.save(createCrewAnnounce(crew, revi));
        announceCacheService.getAnnounce(crew.getId(), announce.getId());

        announceCacheService.getAnnounce(crew.getId(), announce.getId());

        verify(announceQueryDslRepository, times(1)).fetchByIdAndCrewId(crew.getId(), announce.getId());
    }

    @Test
    @DisplayName("기본 공지 목록 조회 시 캐시가 적용되어 반복 호출에도 DB 쿼리가 발생하지 않는다.")
    void getDefaultAnnounces() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        announceRepository.save(createCrewAnnounce(crew, revi));
        announceRepository.save(createCrewAnnounce(crew, revi));
        announceCacheService.getDefaultAnnounces(crew.getId());

        List<AnnounceResponse> results = announceCacheService.getDefaultAnnounces(crew.getId());

        verify(announceQueryDslRepository, times(1)).fetchAllInDefaultByCrewId(crew.getId(), 4);
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("@CachePut은 호출 시마다 로직을 항상 실행하고 캐시된 데이터를 최신 정보로 갱신한다.")
    void putAnnounce() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Announce announce = announceRepository.save(createCrewAnnounce(crew, revi));
        announceCacheService.putAnnounce(crew.getId(), announce.getId());

        announceCacheService.putAnnounce(crew.getId(), announce.getId());

        verify(announceQueryDslRepository, times(2)).fetchByIdAndCrewId(crew.getId(), announce.getId());
    }

    @Test
    @DisplayName("기본 공지 목록을 강제로 갱신할 때마다 레포지토리가 매번 호출된다.")
    void putDefaultAnnounceList() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        announceRepository.save(createCrewAnnounce(crew, revi));
        announceRepository.save(createCrewAnnounce(crew, revi));
        announceCacheService.putDefaultAnnounceList(crew.getId());

        announceCacheService.putDefaultAnnounceList(crew.getId());

        verify(announceQueryDslRepository, times(2)).fetchAllInDefaultByCrewId(crew.getId(), 4);
    }

    private Announce createCrewAnnounce(Crew crew, Member revi) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, revi);
    }
}
