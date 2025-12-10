package revi1337.onsquad.squad_request.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadParticipantFixture.SQUAD_PARTICIPANT;

import java.util.Optional;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

@Import(SquadRequestJdbcRepository.class)
class SquadRequestJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadRequestJpaRepository squadRequestJpaRepository;

    @Test
    @DisplayName("findByIdWithSquad 를 검증한다.")
    void findByIdWithSquad() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        squadRequestJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));

        Optional<SquadRequest> PARTICIPANT = squadRequestJpaRepository.findByIdWithSquad(SQUAD.getId());

        assertThat(PARTICIPANT).isPresent();
        assertThat(PARTICIPANT.get().getSquad()).isNotInstanceOf(HibernateProxy.class);
    }

    @Test
    @DisplayName("findBySquadIdAndCrewMemberId 를 검증한다.")
    void findBySquadIdAndMemberId() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        squadRequestJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));
        clearPersistenceContext();

        Optional<SquadRequest> PARTICIPANT = squadRequestJpaRepository
                .findBySquadIdAndMemberId(SQUAD.getId(), CREW_MEMBER.getId());

        assertThat(PARTICIPANT).isPresent();
        assertThat(PARTICIPANT.get().getSquad()).isInstanceOf(HibernateProxy.class);
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
        squadRequestJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));

        int influenced = squadRequestJpaRepository
                .deleteBySquadIdAndMemberId(SQUAD.getId(), CREW_MEMBER.getId());

        assertThat(influenced).isEqualTo(1);
    }

    @Test
    @DisplayName("deleteById 를 검증한다.")
    void deleteBySquadIdAndMemberId() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        SquadRequest PARTICIPANT = squadRequestJpaRepository.save(SQUAD_PARTICIPANT(SQUAD, CREW_MEMBER));

        squadRequestJpaRepository.deleteById(PARTICIPANT.getId());

        assertThat(squadRequestJpaRepository.findById(PARTICIPANT.getId()))
                .isEmpty();
    }
}
