package revi1337.onsquad.crew_participant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

class CrewParticipantJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewParticipantJpaRepository crewParticipantJpaRepository;

    @Nested
    @DisplayName("Crew 참가신청 save 를 테스트한다.")
    class Save {

        @Test
        @DisplayName("Crew 참가신청 Save 에 성공한다.")
        void save() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());

            CrewParticipant SAVED_PARTICIPANT = crewParticipantJpaRepository.save(CREW_PARTICIPANT);

            assertThat(SAVED_PARTICIPANT.getId()).isNotNull();
        }

        @Test
        @DisplayName("이미 Crew 참가신청을 한적이 있으면 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = new CrewParticipant(CREW, REVI, LocalDateTime.now());
            crewParticipantJpaRepository.save(CREW_PARTICIPANT);
            CrewParticipant DUPLICATE = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());

            assertThatThrownBy(() -> crewParticipantJpaRepository.save(DUPLICATE))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("Crew 참가 신청 조회를 테스트한다.")
    class Find {

        @Test
        @DisplayName("Crew 참가신청 조회에 성공한다.")
        void findByCrewIdAndMemberId() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());
            crewParticipantJpaRepository.save(CREW_PARTICIPANT);

            Optional<CrewParticipant> optionalParticipant = crewParticipantJpaRepository
                    .findByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            assertThat(optionalParticipant).isPresent();
        }
    }

    @Nested
    @DisplayName("Crew 참사 신청 delete 를 테스트한다.")
    class Delete {

        @Test
        @DisplayName("Crew 참가신청 delete 에 성공한다.")
        void deleteById() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());
            CrewParticipant SAVED_PARTICIPANT = crewParticipantJpaRepository.save(CREW_PARTICIPANT);

            crewParticipantJpaRepository.deleteById(SAVED_PARTICIPANT.getId());

            assertThat(crewParticipantJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId())).isEmpty();
        }

        @Test
        @DisplayName("crewId 와 memberId 로 Crew 참가신청 delete 에 성공한다.")
        void deleteByCrewIdAndMemberId() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewParticipant CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());
            CrewParticipant SAVED_PARTICIPANT = crewParticipantJpaRepository.save(CREW_PARTICIPANT);

            crewParticipantJpaRepository.deleteByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            assertThat(crewParticipantJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId())).isEmpty();
        }
    }
}