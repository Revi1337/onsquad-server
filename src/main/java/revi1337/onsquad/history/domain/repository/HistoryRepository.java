package revi1337.onsquad.history.domain.repository;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

public interface HistoryRepository {

    HistoryEntity save(HistoryEntity history);

    Page<HistoryEntity> findAllByMemberIdAndDateRange(Long memberId, LocalDate from, LocalDate to, @Nullable HistoryType type, Pageable pageable);

    void deleteByMemberId(Long memberId);

}
