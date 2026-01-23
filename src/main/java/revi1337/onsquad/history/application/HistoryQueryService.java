package revi1337.onsquad.history.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.history.application.response.HistoryResponse;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryQueryService {

    private final HistoryRepository historyRepository;

    public List<HistoryResponse> fetchHistories(Long memberId, LocalDate from, LocalDate to, HistoryType type) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to == null ? LocalDateTime.of(LocalDate.now(), LocalTime.MAX) : LocalDateTime.of(to, LocalTime.MAX);

        return historyRepository.findHistoriesByMemberIdAndRecordedAtBetween(memberId, start, end, type).stream()
                .map(HistoryResponse::from)
                .toList();
    }
}
