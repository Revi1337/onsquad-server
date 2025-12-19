package revi1337.onsquad.history.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

public interface HistoryRepository {

    HistoryEntity save(HistoryEntity history);

    List<HistoryEntity> findHistoriesByMemberIdAndRecordedAtBetween(Long memberId, LocalDateTime from, LocalDateTime to, HistoryType type);

}
