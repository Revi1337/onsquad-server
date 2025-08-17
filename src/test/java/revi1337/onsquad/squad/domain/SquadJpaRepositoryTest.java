package revi1337.onsquad.squad.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_2;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.GENERAL_SQUAD_MEMBER;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad_member.domain.SquadMemberJpaRepository;

class SquadJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Test
    @DisplayName("스쿼드와 스쿼드멤버를 같이 가져오는데 성공한다.")
    void findByIdWithMembers() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, CREW_MEMBER));
        clearPersistenceContext();

        Optional<Squad> SQUAD_AND_MEMBERS = squadJpaRepository.findByIdWithMembers(SQUAD.getId());

        assertThat(SQUAD_AND_MEMBERS).isPresent();
        assertThat(SQUAD_AND_MEMBERS.get().getMembers()).hasSize(2);
    }

    @Test
    @DisplayName("스쿼드 삭제에 성공한다.")
    void deleteById() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
        clearPersistenceContext();

        squadJpaRepository.deleteById(SQUAD.getId());
        clearPersistenceContext();

        assertThat(squadJpaRepository.findById(SQUAD.getId())).isEmpty();
    }

    @Test
    @DisplayName("크루에 속한 스쿼드 삭제에 성공한다.")
    void deleteByCrewId() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        squadJpaRepository.save(SQUAD(OWNER, CREW));
        squadJpaRepository.save(SQUAD_2(OWNER, CREW));
        clearPersistenceContext();

        squadJpaRepository.deleteByCrewId(CREW.getId());
        clearPersistenceContext();

        assertThat(entityManager.createQuery("select s from Squad s where s.crew.id = :crewId", Squad.class)
                .setParameter("crewId", CREW.getId())
                .getResultList()).isEmpty();
    }
}