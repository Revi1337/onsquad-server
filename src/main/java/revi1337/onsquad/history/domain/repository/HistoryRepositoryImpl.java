package revi1337.onsquad.history.domain.repository;

import java.time.LocalDate;
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

    public List<HistoryEntity> findAllByMemberIdAndDateRange(Long memberId, LocalDate from, LocalDate to, HistoryType type) {
        return historyQueryDslRepository.findAllByMemberIdAndDateRange(memberId, from, to, type);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        historyJpaRepository.deleteByMemberId(memberId);
    }
}
