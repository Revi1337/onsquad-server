package revi1337.onsquad.history.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @Override
    public List<HistoryEntity> findHistoriesByMemberIdAndRecordedAtBetween(Long memberId, LocalDateTime from, LocalDateTime to, HistoryType type) {
        return historyQueryDslRepository.findHistoriesByMemberIdAndRecordedAtBetween(memberId, from, to, type);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        historyJpaRepository.deleteByMemberId(memberId);
    }
}
