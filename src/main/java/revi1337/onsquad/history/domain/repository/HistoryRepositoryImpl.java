package revi1337.onsquad.history.domain.repository;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@Repository
@RequiredArgsConstructor
public class HistoryRepositoryImpl implements HistoryRepository {

    private final HistoryJpaRepository historyJpaRepository;
    private final HistoryQueryDslRepository historyQueryDslRepository;

    @Override
    public HistoryEntity save(HistoryEntity history) {
        return historyJpaRepository.save(history);
    }

    public Page<HistoryEntity> findAllByMemberIdAndDateRange(Long memberId, LocalDate from, LocalDate to, @Nullable HistoryType type, Pageable pageable) {
        return historyQueryDslRepository.findAllByMemberIdAndDateRange(memberId, from, to, type, pageable);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        historyJpaRepository.deleteByMemberId(memberId);
    }
}
