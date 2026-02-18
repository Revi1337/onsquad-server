package revi1337.onsquad.announce.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class AnnounceJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private AnnounceJpaRepository announceRepository;

    @Test
    @DisplayName("회원 ID를 통해 해당 회원이 작성한 모든 공지사항의 참조 정보(CrewId, AnnounceId)를 조회한다")
    void findAnnounceReferencesByMemberId() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        Crew savedCrew = crewRepository.save(crew);
        announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.save(createCrewAnnounce(savedCrew, andong));
        clearPersistenceContext();

        List<AnnounceReference> references = announceRepository.findAnnounceReferencesByMemberId(revi.getId());

        assertThat(references).hasSize(2);

    }

    @Test
    @DisplayName("특정 회원이 작성한 모든 공지사항을 일괄 삭제한다")
    void deleteByMemberId() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        Crew savedCrew = crewRepository.save(crew);
        announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.save(createCrewAnnounce(savedCrew, andong));
        clearPersistenceContext();

        int deleted = announceRepository.deleteByMemberId(revi.getId());

        clearPersistenceContext();
        assertThat(deleted).isEqualTo(2);
    }

    @Test
    @DisplayName("여러 크루 ID 목록에 포함된 모든 공지사항을 일괄 삭제한다")
    void deleteByCrewIdIn() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        Crew savedCrew = crewRepository.save(crew);
        announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.save(createCrewAnnounce(savedCrew, revi));
        announceRepository.save(createCrewAnnounce(savedCrew, andong));
        clearPersistenceContext();

        int deleted = announceRepository.deleteByCrewIdIn(List.of(savedCrew.getId()));

        clearPersistenceContext();
        assertThat(deleted).isEqualTo(3);
    }

    @Test
    @DisplayName("회원이 탈퇴하거나 정보가 사라질 때, 해당 회원이 쓴 공지사항의 작성자 정보를 null로 업데이트한다")
    void markMemberAsNull() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        Crew savedCrew = crewRepository.save(crew);
        Announce announce1 = announceRepository.save(createCrewAnnounce(savedCrew, revi));
        Announce announce2 = announceRepository.save(createCrewAnnounce(savedCrew, revi));
        Announce announce3 = announceRepository.save(createCrewAnnounce(savedCrew, andong));
        clearPersistenceContext();

        int deleted = announceRepository.markMemberAsNull(revi.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(deleted).isEqualTo(2);
            softly.assertThat(announceRepository.findById(announce1.getId()).get().getMember()).isNull();
            softly.assertThat(announceRepository.findById(announce2.getId()).get().getMember()).isNull();
        });
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private Announce createCrewAnnounce(Crew crew, Member revi) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, revi);
    }
}
