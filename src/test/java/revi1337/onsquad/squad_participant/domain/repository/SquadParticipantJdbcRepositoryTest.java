package revi1337.onsquad.squad_participant.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
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
import revi1337.onsquad.squad_participant.domain.entity.SquadParticipant;

@Import(SquadParticipantJdbcRepository.class)
class SquadParticipantJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadParticipantJdbcRepository squadParticipantJdbcRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("스쿼드 참가신청 upsert 를 테스트한다.")
    void success1() {
        // given
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        entityManager.flush();
        entityManager.clear();

        // when
        squadParticipantJdbcRepository.upsertSquadParticipant(SQUAD.getId(), CREW_MEMBER.getId(),
                LocalDateTime.now());

        // then
        assertThat(entityManager
                .createQuery(
                        "select sp from SquadParticipant sp where sp.squad.id = :squadId and sp.crewMember.id = :crewMemberId",
                        SquadParticipant.class)
                .setParameter("squadId", SQUAD.getId())
                .setParameter("crewMemberId", CREW_MEMBER.getId())
                .getSingleResult())
                .isNotNull();
    }

    @Test
    @DisplayName("이미 스쿼드 참가 신청을 했다면, 참가 신청 시간이 업덷이트 된다.")
    void success2() {
        // given
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Squad SQUAD = squadJpaRepository.save(SQUAD(CREW_OWNER, CREW));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        LocalDateTime NOW = LocalDateTime.now();
        squadParticipantJdbcRepository.upsertSquadParticipant(SQUAD.getId(), CREW_MEMBER.getId(), NOW);
        entityManager.flush();
        entityManager.clear();

        // when
        squadParticipantJdbcRepository.upsertSquadParticipant(SQUAD.getId(), CREW_MEMBER.getId(), NOW.plusHours(1));

        // then
        SquadParticipant RESULT = entityManager.createQuery(
                        "select sp from SquadParticipant sp where sp.squad.id = :squadId and sp.crewMember.id = :crewMemberId",
                        SquadParticipant.class)
                .setParameter("squadId", SQUAD.getId())
                .setParameter("crewMemberId", CREW_MEMBER.getId())
                .getSingleResult();
        assertThat(RESULT.getId()).isEqualTo(1L);
    }
}
