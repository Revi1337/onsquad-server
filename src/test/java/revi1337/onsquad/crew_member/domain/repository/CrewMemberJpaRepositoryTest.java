package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewMemberJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Test
    @DisplayName("특정 멤버가 특정 크루에 소속되어 있는지 여부를 확인한다")
    void existsByMemberIdAndCrewId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));

        assertThat(crewMemberRepository.existsByMemberIdAndCrewId(revi.getId(), crew.getId())).isTrue();
    }

    @Test
    @DisplayName("크루 ID와 멤버 ID로 특정 크루원 엔티티를 상세 조회한다")
    void findByCrewIdAndMemberId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));

        Optional<CrewMember> findCrewMember = crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), revi.getId());

        assertSoftly(softly -> {
            softly.assertThat(findCrewMember).isPresent();
            CrewMember crewMember = findCrewMember.get();
            softly.assertThat(crewMember.getCrew().getId()).isEqualTo(crew.getId());
            softly.assertThat(crewMember.getMember().getId()).isEqualTo(revi.getId());
        });
    }

    @Test
    @DisplayName("멤버 ID를 기준으로 해당 멤버의 모든 크루 가입 정보를 삭제한다")
    void deleteByMemberId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));

        int deleted = crewMemberRepository.deleteByMemberId(revi.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            assertThat(deleted).isEqualTo(1);
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), revi.getId())).isEmpty();
        });
    }

    @Test
    @DisplayName("여러 크루 ID 목록에 해당하는 모든 크루원 데이터를 한 번에 삭제한다")
    void deleteByCrewIdIn() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew = createCrew(revi);
        crew.addCrewMember(createGeneralCrewMember(crew, andong));
        crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
        crewRepository.save(crew);

        int deleted = crewMemberRepository.deleteByCrewIdIn(List.of(crew.getId()));

        clearPersistenceContext();
        assertThat(deleted).isEqualTo(3);
    }

    @Test
    @DisplayName("멤버 ID 로 크루원이 있는지 확인한다.")
    void existsCrewMember() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));

        assertThat(crewMemberRepository.existsCrewMember(revi.getId())).isTrue();
    }

    private CrewMember createOwnerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.owner(crew, member, LocalDateTime.now());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
