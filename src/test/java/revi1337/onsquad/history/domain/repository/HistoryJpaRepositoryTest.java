package revi1337.onsquad.history.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewCreateHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createSquadCreateHistory;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;

class HistoryJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private HistoryJpaRepository historyJpaRepository;

    @Test
    @DisplayName("특정 사용자(memberId)의 모든 활동 이력을 일괄 삭제한다")
    void deleteByMemberId() {
        historyJpaRepository.save(createCrewCreateHistory(1L, 1L, "crew-name-1", LocalDateTime.now()));
        historyJpaRepository.save(createCrewCreateHistory(1L, 2L, "crew-name-2", LocalDateTime.now()));
        historyJpaRepository.save(createSquadCreateHistory(1L, 2L, "crew-name-2", 1L, "squad-name-1", LocalDateTime.now()));

        historyJpaRepository.deleteByMemberId(1L);

        assertThat(historyJpaRepository.findAll().size()).isZero();
    }
}
