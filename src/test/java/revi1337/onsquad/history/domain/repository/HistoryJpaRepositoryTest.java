package revi1337.onsquad.history.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

class HistoryJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private HistoryJpaRepository historyJpaRepository;

    @Test
    @DisplayName("특정 사용자(memberId)의 모든 활동 이력을 일괄 삭제한다")
    void deleteByMemberId() {
        historyJpaRepository.save(createCrewCreateHistory(1L, 1L));
        historyJpaRepository.save(createCrewCreateHistory(1L, 2L));
        historyJpaRepository.save(createSquadCreateHistory(1L, 2L, 1L));

        historyJpaRepository.deleteByMemberId(1L);

        assertThat(historyJpaRepository.findAll().size()).isZero();
    }

    private HistoryEntity createCrewCreateHistory(Long memberId, Long crewId) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(HistoryType.CREW_CREATE)
                .message("created-crew")
                .build();
    }

    private HistoryEntity createSquadCreateHistory(Long memberId, Long crewId, Long squadId) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_CREATE)
                .message("created-squad")
                .build();
    }
}
