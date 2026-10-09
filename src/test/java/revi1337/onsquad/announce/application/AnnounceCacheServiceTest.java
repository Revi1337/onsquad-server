package revi1337.onsquad.announce.application;

import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import java.time.LocalDateTime;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
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

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

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
    @DisplayName("작성자가 탈퇴한 공지사항도 단건 조회가 가능하고 작성자는 탈퇴한 회원, role 은 비어 있다.")
    void getAnnounce_whenWriterWithdrawn() {
        Member revi = memberRepository.save(createRevi());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = crewRepository.save(createCrew(revi));
        Announce announce = announceRepository.save(createCrewAnnounce(crew, kwangwon));
        announceRepository.markMemberAsNull(kwangwon.getId());
        clearPersistenceContext();

        AnnounceResponse response = announceCacheService.getAnnounce(crew.getId(), announce.getId());

        assertThat(response.writer()).isEqualTo(SimpleMemberResponse.DELETED_MEMBER);
        assertThat(response.states().role()).isNull();
    }

    @Test
    @DisplayName("작성자가 크루를 떠난 공지사항도 단건 조회가 가능하고 작성자 role 만 비어 있다.")
    void getAnnounce_whenWriterLeftCrew() {
        Member revi = memberRepository.save(createRevi());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = createCrew(revi);
        crew.addCrewMember(CrewMemberFactory.manager(crew, kwangwon, LocalDateTime.now()));
        Crew savedCrew = crewRepository.save(crew);
        Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, kwangwon));
        crewMemberRepository.deleteByMemberId(kwangwon.getId());
        clearPersistenceContext();

        AnnounceResponse response = announceCacheService.getAnnounce(savedCrew.getId(), announce.getId());

        assertThat(response.writer().id()).isEqualTo(kwangwon.getId());
        assertThat(response.states().role()).isNull();
    }

    @Test
    @DisplayName("작성자가 탈퇴했거나 크루를 떠난 공지사항이 있어도 기본 공지 목록 조회가 가능하다.")
    void getDefaultAnnounces_whenWriterWithdrawnOrLeft() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = createCrew(revi);
        crew.addCrewMember(CrewMemberFactory.manager(crew, andong, LocalDateTime.now()));
        crew.addCrewMember(CrewMemberFactory.manager(crew, kwangwon, LocalDateTime.now()));
        Crew savedCrew = crewRepository.save(crew);
        announceRepository.save(createCrewAnnounce(savedCrew, kwangwon));
        announceRepository.save(createCrewAnnounce(savedCrew, andong));
        announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.markMemberAsNull(kwangwon.getId());
        crewMemberRepository.deleteByMemberId(andong.getId());
        clearPersistenceContext();

        List<AnnounceResponse> results = announceCacheService.getDefaultAnnounces(savedCrew.getId());

        assertThat(results).hasSize(3);
        assertThat(results).filteredOn(r -> r.writer().equals(SimpleMemberResponse.DELETED_MEMBER)).hasSize(1);
        assertThat(results).filteredOn(r -> r.states().role() == null).hasSize(2);
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
