package revi1337.onsquad.squad.domain.repository;

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
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;

@ExtendWith(MockitoExtension.class)
class SquadRepositoryImplTest {

    @Mock
    private SquadJpaRepository squadJpaRepository;

    @Mock
    private SquadQueryDslRepository squadQueryDslRepository;

    @InjectMocks
    private SquadRepositoryImpl squadRepository;

    @Test
    @DisplayName("save 위임에 성공한다.")
    void save() {
        Squad squad = mock(Squad.class);

        squadRepository.save(squad);

        verify(squadJpaRepository).save(squad);
    }

    @Test
    @DisplayName("saveAndFlush 위임에 성공한다.")
    void saveAndFlush() {
        Squad squad = mock(Squad.class);

        squadRepository.saveAndFlush(squad);

        verify(squadJpaRepository).saveAndFlush(squad);
    }

    @Nested
    @DisplayName("findById & getById 위임을 테스트한다.")
    class FindAndGetById {

        @Test
        @DisplayName("fetchById 위임에 성공한다.")
        void success1() {
            Long squadId = 1L;

            squadRepository.findById(squadId);

            verify(squadJpaRepository).findById(squadId);
        }

        @Test
        @DisplayName("getSquadById 위임에 성공한다.")
        void success2() {
            Long squadId = 1L;
            Squad squad = mock(Squad.class);
            when(squadJpaRepository.findById(squadId)).thenReturn(Optional.of(squad));

            squadRepository.getById(squadId);

            verify(squadJpaRepository).findById(squadId);
        }

        @Test
        @DisplayName("getSquadById 를 호출했을 때, fetchById 의 결과가 없으면 실패한다.")
        void fail1() {
            Long squadId = 1L;
            when(squadRepository.findById(squadId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> squadRepository.getById(squadId))
                    .isExactlyInstanceOf(SquadBusinessException.NotFound.class);
        }
    }

    @Nested
    @DisplayName("findByIdWithMembers & getByIdWithMembers 위임을 테스트한다.")
    class FindAndGetByIdWithMembers {

        @Test
        @DisplayName("findByIdWithMembers 위임에 성공한다.")
        void success1() {
            Long squadId = 1L;

            squadRepository.findByIdWithMembers(squadId);

            verify(squadJpaRepository).findByIdWithMembers(squadId);
        }

        @Test
        @DisplayName("getByIdWithMembers 위임에 성공한다.")
        void success2() {
            Long squadId = 1L;
            Squad squad = mock(Squad.class);
            when(squadJpaRepository.findByIdWithMembers(squadId)).thenReturn(Optional.of(squad));

            squadRepository.getByIdWithMembers(squadId);

            verify(squadJpaRepository).findByIdWithMembers(squadId);
        }

        @Test
        @DisplayName("getByIdWithMembers 를 호출했을 때, findByIdWithMembers 의 결과가 없으면 실패한다.")
        void fail() {
            Long squadId = 1L;
            when(squadRepository.findByIdWithMembers(squadId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> squadRepository.getByIdWithMembers(squadId))
                    .isExactlyInstanceOf(SquadBusinessException.NotFound.class);
        }
    }

    @Nested
    @DisplayName("fetchById & getSquadById 위임을 테스트한다.")
    class FetchAndGetById {

        @Test
        @DisplayName("fetchById 위임에 성공한다.")
        void success1() {
            Long squadId = 1L;

            squadRepository.fetchById(squadId);

            verify(squadQueryDslRepository).fetchById(squadId);
        }

        @Test
        @DisplayName("getSquadById 위임에 성공한다.")
        void success2() {
            Long squadId = 1L;
            SquadDomainDto squad = mock(SquadDomainDto.class);
            when(squadQueryDslRepository.fetchById(squadId)).thenReturn(Optional.of(squad));

            squadRepository.getSquadById(squadId);

            verify(squadQueryDslRepository).fetchById(squadId);
        }

        @Test
        @DisplayName("getSquadById 를 호출했을 때, fetchById 의 결과가 없으면 실패한다.")
        void fail1() {
            Long squadId = 1L;
            when(squadRepository.fetchById(squadId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> squadRepository.getSquadById(squadId))
                    .isExactlyInstanceOf(SquadBusinessException.NotFound.class);
        }
    }

    @Test
    @DisplayName("fetchAllByCrewId 위임에 성공한다.")
    void fetchAllByCrewId() {
        Long crewId = 1L;
        CategoryType categoryType = CategoryType.ACTIVITY;
        PageRequest pageRequest = PageRequest.of(0, 2);

        squadRepository.fetchAllByCrewId(crewId, categoryType, pageRequest);

        verify(squadQueryDslRepository).fetchAllByCrewId(crewId, categoryType, pageRequest);
    }

    @Test
    @DisplayName("fetchAllWithOwnerState 위임에 성공한다.")
    void fetchAllWithOwnerState() {
        Long memberId = 1L;
        Long crewId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 2);

        squadRepository.fetchAllWithOwnerState(memberId, crewId, pageRequest);

        verify(squadQueryDslRepository).fetchAllWithOwnerState(memberId, crewId, pageRequest);
    }
}
