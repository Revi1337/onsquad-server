package revi1337.onsquad.crew_participant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@Import(CrewParticipantJdbcRepository.class)
class CrewParticipantJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @SpyBean
    private CrewParticipantJpaRepository crewParticipantJpaRepository;

    @Autowired
    private CrewParticipantJdbcRepository crewParticipantJdbcRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Nested
    @DisplayName("Crew 참가신청 Upsert 를 테스트한다.")
    class Upsert {

        @Test
        @DisplayName("Crew 에 신청해 본적이 없다면, insert 된다.")
        void success1() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            LocalDateTime now = LocalDateTime.now();

            crewParticipantJdbcRepository.upsertCrewParticipant(CREW.getId(), REVI.getId(), now);

            assertThat(crewParticipantJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("Crew 에 이미 신청을 했다면, Update 가 된다.")
        void success2() {
            // given
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            LocalDateTime NOW = LocalDateTime.now();
            crewParticipantJpaRepository.save(CREW_PARTICIPANT(CREW, REVI, NOW));

            // when
            crewParticipantJdbcRepository.upsertCrewParticipant(CREW.getId(), REVI.getId(), NOW.plusHours(1));
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(crewParticipantJpaRepository
                    .findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get().getRequestAt())
                    .isEqualTo(NOW.plusHours(1));
        }
    }
}
