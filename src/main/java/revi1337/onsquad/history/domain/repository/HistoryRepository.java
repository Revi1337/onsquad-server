package revi1337.onsquad.history.domain.repository;

import java.time.LocalDate;
import java.util.List;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

public interface HistoryRepository {

    HistoryEntity save(HistoryEntity history);

    List<HistoryEntity> findAllByMemberIdAndDateRange(Long memberId, LocalDate from, LocalDate to, HistoryType type);

    void deleteByMemberId(Long memberId);

}
