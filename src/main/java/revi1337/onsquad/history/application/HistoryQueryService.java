package revi1337.onsquad.history.application;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.history.application.response.HistoryResponse;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.repository.HistoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryQueryService {

    private final HistoryRepository historyRepository;

    public PageResponse<HistoryResponse> fetchHistories(Long memberId, LocalDate from, LocalDate to, @Nullable HistoryType type, Pageable pageable) {
        Page<HistoryResponse> historyResponse = historyRepository.findAllByMemberIdAndDateRange(memberId, from, to, type, pageable)
                .map(HistoryResponse::from);

        return PageResponse.from(historyResponse);
    }
}
