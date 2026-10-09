package revi1337.onsquad.announce.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithPinAndModifyStateResponse;
import revi1337.onsquad.announce.application.dto.response.AnnouncesWithWriteStateResponse;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewRole;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class AnnounceQueryServiceWithdrawnWriterTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private AnnounceQueryService announceQueryService;

    @Test
    @DisplayName("작성자가 탈퇴한 공지사항을 상세 조회하면 작성자는 탈퇴한 회원, role 은 비어 있고, owner 만 canModify: true")
    void findAnnounce_whenWriterWithdrawn() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
        Crew savedCrew = crewRepository.save(crew);
        Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, kwangwon));
        announceRepository.markMemberAsNull(kwangwon.getId());
        clearPersistenceContext();

        AnnounceWithPinAndModifyStateResponse ownerView = announceQueryService.findAnnounce(revi.getId(), crew.getId(), announce.getId());
        AnnounceWithPinAndModifyStateResponse managerView = announceQueryService.findAnnounce(andong.getId(), crew.getId(), announce.getId());

        assertSoftly(softly -> {
            softly.assertThat(ownerView.writer()).isEqualTo(SimpleMemberResponse.DELETED_MEMBER);
            softly.assertThat(ownerView.states().role()).isNull();
            softly.assertThat(ownerView.states().canModify()).isTrue();
            softly.assertThat(managerView.writer()).isEqualTo(SimpleMemberResponse.DELETED_MEMBER);
            softly.assertThat(managerView.states().canModify()).isFalse();
        });
    }

    @Test
    @DisplayName("작성자가 크루를 떠났어도 공지사항을 상세 조회할 수 있고 작성자 role 만 비어 있다.")
    void findAnnounce_whenWriterLeftCrew() {
        Member revi = memberRepository.save(createRevi());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
        Crew savedCrew = crewRepository.save(crew);
        Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, kwangwon));
        crewMemberRepository.deleteByMemberId(kwangwon.getId());
        clearPersistenceContext();

        AnnounceWithPinAndModifyStateResponse response = announceQueryService.findAnnounce(revi.getId(), crew.getId(), announce.getId());

        assertSoftly(softly -> {
            softly.assertThat(response.id()).isEqualTo(announce.getId());
            softly.assertThat(response.writer().id()).isEqualTo(kwangwon.getId());
            softly.assertThat(response.states().role()).isNull();
        });
    }

    @Test
    @DisplayName("작성자가 탈퇴했거나 크루를 떠난 공지사항이 섞여 있어도 목록 조회가 가능하다.")
    void findAnnounces() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
        Crew savedCrew = crewRepository.save(crew);
        Announce withdrawn = announceRepository.save(createCrewAnnounce(savedCrew, kwangwon));
        Announce left = announceRepository.save(createCrewAnnounce(savedCrew, andong));
        Announce normal = announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.markMemberAsNull(kwangwon.getId());
        crewMemberRepository.deleteByMemberId(andong.getId());
        clearPersistenceContext();

        AnnouncesWithWriteStateResponse response = announceQueryService.findAnnounces(revi.getId(), crew.getId());

        assertSoftly(softly -> {
            softly.assertThat(response.announces()).hasSize(3);
            softly.assertThat(response.announces()).filteredOn(a -> a.id().equals(withdrawn.getId()))
                    .singleElement()
                    .satisfies(a -> {
                        softly.assertThat(a.writer()).isEqualTo(SimpleMemberResponse.DELETED_MEMBER);
                        softly.assertThat(a.states().role()).isNull();
                    });
            softly.assertThat(response.announces()).filteredOn(a -> a.id().equals(left.getId()))
                    .singleElement()
                    .satisfies(a -> softly.assertThat(a.states().role()).isNull());
            softly.assertThat(response.announces()).filteredOn(a -> a.id().equals(normal.getId()))
                    .singleElement()
                    .satisfies(a -> softly.assertThat(a.states().role()).isEqualTo(CrewRole.OWNER));
        });
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private Announce createCrewAnnounce(Crew crew, Member writer) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, writer);
    }

    @TestConfiguration
    static class CacheTestConfig {

        @Bean("redisCacheManager")
        public CacheManager cacheManager() {
            return new NoOpCacheManager();
        }

        @Bean
        public AnnounceCacheEvictor mockAnnounceCacheEvictor() {
            return new AnnounceCacheEvictor() {
                @Override
                public boolean supports(CacheManager cacheManager) {
                    return cacheManager instanceof NoOpCacheManager;
                }

                @Override
                public void evictAnnounce(Long crewId, Long announceId) {

                }

                @Override
                public void evictAnnounces(Long crewId) {

                }

                @Override
                public void evictAnnounces(List<Long> crewIds) {

                }

                @Override
                public void evictAnnouncesByReferences(List<AnnounceReference> references) {

                }

                @Override
                public void evictAnnounceLists(List<Long> crewIds) {

                }
            };
        }
    }
}
