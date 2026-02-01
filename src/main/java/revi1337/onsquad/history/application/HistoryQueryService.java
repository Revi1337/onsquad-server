package revi1337.onsquad.history.application;

import java.time.LocalDate;
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
        return historyRepository.findAllByMemberIdAndDateRange(memberId, from, to, type).stream()
                .map(HistoryResponse::from)
                .toList();
    }
}
