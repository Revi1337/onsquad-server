package revi1337.onsquad.crew_request.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewRequestJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestJpaRepository;

    @Nested
    @DisplayName("Crew 참가신청 save 를 테스트한다.")
    class Save {

        @Test
        @DisplayName("Crew 참가신청 Save 에 성공한다.")
        void save() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewRequest CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());

            CrewRequest SAVED_PARTICIPANT = crewRequestJpaRepository.save(CREW_PARTICIPANT);

            assertThat(SAVED_PARTICIPANT.getId()).isNotNull();
        }

        @Test
        @DisplayName("이미 Crew 참가신청을 한적이 있으면 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewRequest CREW_PARTICIPANT = new CrewRequest(CREW, REVI, LocalDateTime.now());
            crewRequestJpaRepository.save(CREW_PARTICIPANT);
            CrewRequest DUPLICATE = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());

            assertThatThrownBy(() -> crewRequestJpaRepository.save(DUPLICATE))
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
            CrewRequest CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());
            crewRequestJpaRepository.save(CREW_PARTICIPANT);

            Optional<CrewRequest> optionalParticipant = crewRequestJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            assertThat(optionalParticipant).isPresent();
        }
    }

    @Nested
    @DisplayName("Crew 참가 신청 + Crew 페치 조인 조회를 테스트한다.")
    class FindWithCrew {

        @Test
        @DisplayName("Crew 참가 신청 + Crew 페치 조인 조회에 성공한다.")
        void findWithCrewById() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewRequest CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());
            crewRequestJpaRepository.save(CREW_PARTICIPANT);

            Optional<CrewRequest> optionalParticipant = crewRequestJpaRepository.findWithCrewById(CREW.getId());

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
            CrewRequest CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());
            CrewRequest SAVED_PARTICIPANT = crewRequestJpaRepository.save(CREW_PARTICIPANT);

            crewRequestJpaRepository.deleteById(SAVED_PARTICIPANT.getId());

            assertThat(crewRequestJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId())).isEmpty();
        }

        @Test
        @DisplayName("crewId 와 memberId 로 Crew 참가신청 delete 에 성공한다.")
        void deleteByCrewIdAndMemberId() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewRequest CREW_PARTICIPANT = CREW_PARTICIPANT(CREW, REVI, LocalDateTime.now());
            CrewRequest SAVED_PARTICIPANT = crewRequestJpaRepository.save(CREW_PARTICIPANT);

            crewRequestJpaRepository.deleteByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            assertThat(crewRequestJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId())).isEmpty();
        }
    }
}
