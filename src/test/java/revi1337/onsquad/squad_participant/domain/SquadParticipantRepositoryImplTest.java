package revi1337.onsquad.squad_participant.domain;

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
import revi1337.onsquad.squad_participant.error.exception.SquadParticipantBusinessException;

@ExtendWith(MockitoExtension.class)
class SquadParticipantRepositoryImplTest {

    @Mock
    private SquadParticipantJpaRepository squadParticipantJpaRepository;

    @Mock
    private SquadParticipantJdbcRepository squadParticipantJdbcRepository;

    @Mock
    private SquadParticipantQueryDslRepository squadParticipantQueryDslRepository;

    @InjectMocks
    private SquadParticipantRepositoryImpl squadParticipantRepository;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void save() {
        SquadParticipant participant = mock(SquadParticipant.class);

        squadParticipantRepository.save(participant);

        verify(squadParticipantJpaRepository).save(participant);
    }

    @Test
    @DisplayName("saveAndFlush 위임에 성공한다.")
    void saveAndFlush() {
        SquadParticipant participant = mock(SquadParticipant.class);

        squadParticipantRepository.saveAndFlush(participant);

        verify(squadParticipantJpaRepository).saveAndFlush(participant);
    }

    @Test
    @DisplayName("findById 위임에 성공한다.")
    void findById() {
        Long participantId = 1L;

        squadParticipantRepository.findById(participantId);

        verify(squadParticipantJpaRepository).findById(participantId);
    }

    @Nested
    @DisplayName("findBySquadIdAndCrewMemberId & getBySquadIdAndCrewMemberId 위임을 테스트한다.")
    class FindAndGetBySquadIdAndCrewMemberId {

        @Test
        @DisplayName("findBySquadIdAndCrewMemberId 위임에 성공한다.")
        void findBySquadIdAndCrewMemberId() {
            Long squadId = 1L;
            Long crewMemberId = 2L;

            squadParticipantRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId);

            verify(squadParticipantJpaRepository).findBySquadIdAndCrewMemberId(squadId, crewMemberId);
        }

        @Test
        @DisplayName("getBySquadIdAndCrewMemberId 위임에 성공한다.")
        void getBySquadIdAndCrewMemberId() {
            Long squadId = 1L;
            Long crewMemberId = 2L;
            SquadParticipant participant = mock(SquadParticipant.class);
            when(squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId))
                    .thenReturn(Optional.of(participant));

            squadParticipantRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId);

            verify(squadParticipantJpaRepository).findBySquadIdAndCrewMemberId(squadId, crewMemberId);
        }

        @Test
        @DisplayName("getBySquadIdAndCrewMemberId 를 호출했을 때, findBySquadIdAndCrewMemberId 의 결과가 없으면 실패한다.")
        void getBySquadIdAndCrewMemberIdFail() {
            Long squadId = 1L;
            Long crewMemberId = 2L;
            when(squadParticipantJpaRepository.findBySquadIdAndCrewMemberId(squadId, crewMemberId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> squadParticipantRepository.getBySquadIdAndCrewMemberId(squadId, crewMemberId))
                    .isExactlyInstanceOf(SquadParticipantBusinessException.NeverRequested.class);
        }
    }

    @Test
    @DisplayName("findSquadParticipantRequestsByMemberId 위임에 성공한다.")
    void findSquadParticipantRequestsByMemberId() {
        Long memberId = 1L;

        squadParticipantRepository.findSquadParticipantRequestsByMemberId(memberId);

        verify(squadParticipantQueryDslRepository).findSquadParticipantRequestsByMemberId(memberId);
    }

    @Test
    @DisplayName("fetchAllBySquadId 위임에 성공한다.")
    void fetchAllBySquadId() {
        Long squadId = 1L;
        PageRequest pageRequest = mock(PageRequest.class);

        squadParticipantRepository.fetchAllBySquadId(squadId, pageRequest);

        verify(squadParticipantQueryDslRepository).fetchAllBySquadId(squadId, pageRequest);
    }

    @Test
    @DisplayName("deleteBySquadIdCrewMemberId 위임에 성공한다.")
    void deleteBySquadIdCrewMemberId() {
        Long squadId = 1L;
        Long crewMemberId = 2L;

        squadParticipantRepository.deleteBySquadIdCrewMemberId(squadId, crewMemberId);

        verify(squadParticipantJpaRepository).deleteBySquadIdAndCrewMemberId(squadId, crewMemberId);
    }

    @Test
    @DisplayName("deleteById 위임에 성공한다.")
    void deleteById() {
        Long participantId = 1L;

        squadParticipantRepository.deleteById(participantId);

        verify(squadParticipantJpaRepository).deleteById(participantId);
    }
}