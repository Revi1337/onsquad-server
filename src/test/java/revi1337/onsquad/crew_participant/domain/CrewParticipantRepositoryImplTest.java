package revi1337.onsquad.crew_participant.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;

@ExtendWith(MockitoExtension.class)
class CrewParticipantRepositoryImplTest {

    @Mock
    private CrewParticipantJpaRepository crewParticipantJpaRepository;

    @Mock
    private CrewParticipantQueryDslRepository crewParticipantQueryDslRepository;

    @InjectMocks
    private CrewParticipantRepositoryImpl crewParticipantRepository;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void save() {
        CrewParticipant crewParticipant = mock(CrewParticipant.class);

        crewParticipantRepository.save(crewParticipant);

        verify(crewParticipantJpaRepository).save(crewParticipant);
    }

    @Test
    @DisplayName("saveAndFlush 위임에 성공한다.")
    void saveAndFlush() {
        CrewParticipant crewParticipant = mock(CrewParticipant.class);

        crewParticipantRepository.saveAndFlush(crewParticipant);

        verify(crewParticipantJpaRepository).saveAndFlush(crewParticipant);
    }

    @Nested
    @DisplayName("findById & getById 위임을 테스트한다.")
    class FindAndGetById {

        @Test
        @DisplayName("findById 위임에 성공한다.")
        void findById() {
            Long participantId = 1L;

            crewParticipantRepository.findById(participantId);

            verify(crewParticipantJpaRepository).findById(participantId);
        }

        @Test
        @DisplayName("getById 위임에 성공한다.")
        void getById() {
            Long participantId = 1L;
            CrewParticipant crewParticipant = mock(CrewParticipant.class);
            when(crewParticipantJpaRepository.findById(participantId)).thenReturn(Optional.of(crewParticipant));

            crewParticipantRepository.getById(participantId);

            verify(crewParticipantJpaRepository).findById(participantId);
        }

        @Test
        @DisplayName("getById 를 호출했을 때, findById 의 결과가 없으면 실패한다.")
        void getByIdFail() {
            Long participantId = 1L;
            when(crewParticipantJpaRepository.findById(participantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewParticipantRepository.getById(participantId))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.NeverRequested.class);
        }
    }

    @Nested
    @DisplayName("findWithCrewById & getWithCrewById 위임을 테스트한다.")
    class FindWithCrewAndGetWithCrewById {

        @Test
        @DisplayName("findWithCrewById 위임에 성공한다.")
        void findWithCrewById() {
            Long participantId = 1L;
            CrewParticipant crewParticipant = mock(CrewParticipant.class);
            when(crewParticipantJpaRepository.findWithCrewById(participantId)).thenReturn(Optional.of(crewParticipant));

            crewParticipantRepository.findWithCrewById(participantId);

            verify(crewParticipantJpaRepository).findWithCrewById(participantId);
        }

        @Test
        @DisplayName("getWithCrewById 위임에 성공한다.")
        void getById() {
            Long participantId = 1L;
            CrewParticipant crewParticipant = mock(CrewParticipant.class);
            when(crewParticipantJpaRepository.findWithCrewById(participantId)).thenReturn(Optional.of(crewParticipant));

            crewParticipantRepository.getWithCrewById(participantId);

            verify(crewParticipantJpaRepository).findWithCrewById(participantId);
        }

        @Test
        @DisplayName("getById 를 호출했을 때, findById 의 결과가 없으면 실패한다.")
        void getByIdFail() {
            Long participantId = 1L;
            when(crewParticipantJpaRepository.findWithCrewById(participantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewParticipantRepository.getWithCrewById(participantId))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.NeverRequested.class);
        }
    }

    @Test
    @DisplayName("deleteById 위임에 성공한다.")
    void deleteById() {
        Long participantId = 1L;

        crewParticipantRepository.deleteById(participantId);

        verify(crewParticipantJpaRepository).deleteById(participantId);
    }

    @Nested
    @DisplayName("findByCrewIdAndMemberId & getByCrewIdAndMemberId 위임을 테스트한다.")
    class FindAndGetByCrewIdAndMemberId {

        @Test
        @DisplayName("findByCrewIdAndMemberId 위임에 성공한다.")
        void findByCrewIdAndMemberId() {
            Long crewId = 1L;
            Long memberId = 2L;

            crewParticipantRepository.findByCrewIdAndMemberId(crewId, memberId);

            verify(crewParticipantJpaRepository).findByCrewIdAndMemberId(crewId, memberId);
        }

        @Test
        @DisplayName("getByCrewIdAndMemberId 위임에 성공한다.")
        void getByCrewIdAndMemberId() {
            Long crewId = 1L;
            Long memberId = 2L;
            CrewParticipant crewParticipant = mock(CrewParticipant.class);
            when(crewParticipantJpaRepository.findByCrewIdAndMemberId(crewId, memberId))
                    .thenReturn(Optional.of(crewParticipant));

            crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId);

            verify(crewParticipantJpaRepository).findByCrewIdAndMemberId(crewId, memberId);
        }

        @Test
        @DisplayName("getByCrewIdAndMemberId 를 호출했을 때, findByCrewIdAndMemberId 의 결과가 없으면 실패한다.")
        void getByCrewIdAndMemberIdFail() {
            Long crewId = 1L;
            Long memberId = 2L;
            when(crewParticipantJpaRepository.findByCrewIdAndMemberId(crewId, memberId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId))
                    .isExactlyInstanceOf(CrewParticipantBusinessException.NeverRequested.class);
        }
    }

    @Test
    @DisplayName("fetchAllWithSimpleCrewByMemberId 위임에 성공한다.")
    void fetchAllWithSimpleCrewByMemberId() {
        Long memberId = 2L;

        crewParticipantRepository.fetchAllWithSimpleCrewByMemberId(memberId);

        verify(crewParticipantQueryDslRepository).fetchAllWithSimpleCrewByMemberId(memberId);
    }

    @Test
    @DisplayName("fetchCrewRequests 위임에 성공한다.")
    void fetchCrewRequests() {
        Long crewId = 1L;
        PageRequest pageRequest = mock(PageRequest.class);

        crewParticipantRepository.fetchCrewRequests(crewId, pageRequest);

        verify(crewParticipantQueryDslRepository).fetchCrewRequests(crewId, pageRequest);
    }
}
