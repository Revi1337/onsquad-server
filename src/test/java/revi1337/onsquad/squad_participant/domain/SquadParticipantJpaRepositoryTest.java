package revi1337.onsquad.squad_participant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadParticipantFixture.SQUAD_PARTICIPANT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadJpaRepository;

@Import(SquadParticipantJdbcRepository.class)
class SquadParticipantJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadParticipantJpaRepository squadParticipantJpaRepository;

    @Test
    @DisplayName("deleteBySquadIdAndCrewMemberId 를 검증한다.")
    void findBySquadIdAndCrewMemberId() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));

        assertThat(squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER.getId()))
                .isPresent();
    }

    @Test
    @DisplayName("deleteBySquadIdAndCrewMemberId 를 검증한다.")
    void deleteById() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));

        int influenced = squadParticipantJpaRepository
                .deleteBySquadIdAndCrewMemberId(SQUAD.getId(), CREW_MEMBER.getId());

        assertThat(influenced).isEqualTo(1);
    }

    @Test
    @DisplayName("deleteById 를 검증한다.")
    void deleteBySquadIdAndCrewMemberId() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        SquadParticipant PARTICIPANT = squadParticipantJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));

        squadParticipantJpaRepository.deleteById(PARTICIPANT.getId());

        assertThat(squadParticipantJpaRepository.findById(PARTICIPANT.getId()))
                .isEmpty();
    }
}
