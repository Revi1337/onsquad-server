package revi1337.onsquad.crew_request.domain.repository;

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
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.error.exception.CrewRequestBusinessException;

@ExtendWith(MockitoExtension.class)
class CrewRequestRepositoryImplTest {

    @Mock
    private CrewRequestJpaRepository crewRequestJpaRepository;

    @Mock
    private CrewRequestQueryDslRepository crewRequestQueryDslRepository;

    @InjectMocks
    private CrewRequestRepositoryImpl crewParticipantRepository;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void save() {
        CrewRequest crewRequest = mock(CrewRequest.class);

        crewParticipantRepository.save(crewRequest);

        verify(crewRequestJpaRepository).save(crewRequest);
    }

    @Test
    @DisplayName("saveAndFlush 위임에 성공한다.")
    void saveAndFlush() {
        CrewRequest crewRequest = mock(CrewRequest.class);

        crewParticipantRepository.saveAndFlush(crewRequest);

        verify(crewRequestJpaRepository).saveAndFlush(crewRequest);
    }

    @Nested
    @DisplayName("findById & getById 위임을 테스트한다.")
    class FindAndGetById {

        @Test
        @DisplayName("findById 위임에 성공한다.")
        void findById() {
            Long participantId = 1L;

            crewParticipantRepository.findById(participantId);

            verify(crewRequestJpaRepository).findById(participantId);
        }

        @Test
        @DisplayName("getById 위임에 성공한다.")
        void getById() {
            Long participantId = 1L;
            CrewRequest crewRequest = mock(CrewRequest.class);
            when(crewRequestJpaRepository.findById(participantId)).thenReturn(Optional.of(crewRequest));

            crewParticipantRepository.getById(participantId);

            verify(crewRequestJpaRepository).findById(participantId);
        }

        @Test
        @DisplayName("getById 를 호출했을 때, findById 의 결과가 없으면 실패한다.")
        void getByIdFail() {
            Long participantId = 1L;
            when(crewRequestJpaRepository.findById(participantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewParticipantRepository.getById(participantId))
                    .isExactlyInstanceOf(CrewRequestBusinessException.NeverRequested.class);
        }
    }

    @Nested
    @DisplayName("findWithCrewById & getWithCrewById 위임을 테스트한다.")
    class FindWithCrewAndGetWithCrewById {

        @Test
        @DisplayName("findWithCrewById 위임에 성공한다.")
        void findWithCrewById() {
            Long participantId = 1L;
            CrewRequest crewRequest = mock(CrewRequest.class);
            when(crewRequestJpaRepository.findWithCrewById(participantId)).thenReturn(Optional.of(crewRequest));

            crewParticipantRepository.findWithCrewById(participantId);

            verify(crewRequestJpaRepository).findWithCrewById(participantId);
        }

        @Test
        @DisplayName("getWithCrewById 위임에 성공한다.")
        void getById() {
            Long participantId = 1L;
            CrewRequest crewRequest = mock(CrewRequest.class);
            when(crewRequestJpaRepository.findWithCrewById(participantId)).thenReturn(Optional.of(crewRequest));

            crewParticipantRepository.getWithCrewById(participantId);

            verify(crewRequestJpaRepository).findWithCrewById(participantId);
        }

        @Test
        @DisplayName("getById 를 호출했을 때, findById 의 결과가 없으면 실패한다.")
        void getByIdFail() {
            Long participantId = 1L;
            when(crewRequestJpaRepository.findWithCrewById(participantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewParticipantRepository.getWithCrewById(participantId))
                    .isExactlyInstanceOf(CrewRequestBusinessException.NeverRequested.class);
        }
    }

    @Test
    @DisplayName("deleteById 위임에 성공한다.")
    void deleteById() {
        Long participantId = 1L;

        crewParticipantRepository.deleteById(participantId);

        verify(crewRequestJpaRepository).deleteById(participantId);
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

            verify(crewRequestJpaRepository).findByCrewIdAndMemberId(crewId, memberId);
        }

        @Test
        @DisplayName("getByCrewIdAndMemberId 위임에 성공한다.")
        void getByCrewIdAndMemberId() {
            Long crewId = 1L;
            Long memberId = 2L;
            CrewRequest crewRequest = mock(CrewRequest.class);
            when(crewRequestJpaRepository.findByCrewIdAndMemberId(crewId, memberId))
                    .thenReturn(Optional.of(crewRequest));

            crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId);

            verify(crewRequestJpaRepository).findByCrewIdAndMemberId(crewId, memberId);
        }

        @Test
        @DisplayName("getByCrewIdAndMemberId 를 호출했을 때, findByCrewIdAndMemberId 의 결과가 없으면 실패한다.")
        void getByCrewIdAndMemberIdFail() {
            Long crewId = 1L;
            Long memberId = 2L;
            when(crewRequestJpaRepository.findByCrewIdAndMemberId(crewId, memberId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId))
                    .isExactlyInstanceOf(CrewRequestBusinessException.NeverRequested.class);
        }
    }

    @Test
    @DisplayName("fetchAllWithSimpleCrewByMemberId 위임에 성공한다.")
    void fetchAllWithSimpleCrewByMemberId() {
        Long memberId = 2L;

        crewParticipantRepository.fetchAllWithSimpleCrewByMemberId(memberId);

        verify(crewRequestQueryDslRepository).fetchAllWithSimpleCrewByMemberId(memberId);
    }

    @Test
    @DisplayName("fetchCrewRequests 위임에 성공한다.")
    void fetchCrewRequests() {
        Long crewId = 1L;
        PageRequest pageRequest = mock(PageRequest.class);

        crewParticipantRepository.fetchCrewRequests(crewId, pageRequest);

        verify(crewRequestQueryDslRepository).fetchCrewRequests(crewId, pageRequest);
    }
}
