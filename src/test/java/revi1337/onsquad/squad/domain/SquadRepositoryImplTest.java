package revi1337.onsquad.squad.domain;

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
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
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

    @Nested
    @DisplayName("fetchById & getSquadById 위임을 테스트한다.")
    class GetAndFetchById {

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
            SquadInfoDomainDto squad = mock(SquadInfoDomainDto.class);
            when(squadQueryDslRepository.fetchById(squadId)).thenReturn(Optional.of(squad));

            squadRepository.getSquadById(squadId);

            verify(squadQueryDslRepository).fetchById(squadId);
        }

        @Test
        @DisplayName("getSquadById 를 호출했을떄, fetchById 의 결과가 없으면 실패한다.")
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