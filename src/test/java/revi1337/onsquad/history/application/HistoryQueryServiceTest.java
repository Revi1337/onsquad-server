package revi1337.onsquad.history.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewCreateHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createSquadCreateHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.history.application.response.HistoryResponse;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.repository.HistoryJpaRepository;

class HistoryQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private HistoryJpaRepository historyRepository;

    @Autowired
    private HistoryQueryService historyQueryService;

    @Test
    @DisplayName("사용자 ID, 기간, 활동 유형 조건에 부합하는 활동 이력 목록을 조회한다")
    void fetchHistories() {
        LocalDate baseRecordedAt = LocalDate.of(2026, 1, 4);
        LocalDateTime fromDateTime = baseRecordedAt.atStartOfDay();
        LocalDateTime toDateTime = baseRecordedAt.plusDays(6).atTime(23, 59, 59);
        historyRepository.save(createCrewCreateHistory(1L, 3L, "crew-name-3", fromDateTime));
        historyRepository.save(createCrewCreateHistory(2L, 3L, "crew-name-3", fromDateTime));
        historyRepository.save(createCrewCreateHistory(2L, 4L, "crew-name-4", toDateTime));
        historyRepository.save(createSquadCreateHistory(2L, 4L, "crew-name-4", 1L, "squad-name-1", fromDateTime));
        historyRepository.save(createSquadCreateHistory(2L, 4L, "crew-name-4", 2L, "squad-name-2", toDateTime));
        clearPersistenceContext();
        PageRequest pageable = PageRequest.of(0, 2, Sort.by("recordedAt").descending());

        PageResponse<HistoryResponse> response = historyQueryService.fetchHistories(
                2L,
                baseRecordedAt,
                baseRecordedAt.plusDays(6),
                HistoryType.SQUAD_CREATE,
                pageable
        );

        assertSoftly(softly -> {
            softly.assertThat(response.size()).isEqualTo(2);
            softly.assertThat(response.page()).isEqualTo(1);
            softly.assertThat(response.totalPages()).isEqualTo(1);
            softly.assertThat(response.totalCount()).isEqualTo(2);
            softly.assertThat(response.resultsSize()).isEqualTo(2);

            List<HistoryResponse> results = response.results();
            softly.assertThat(results.get(0).type()).isSameAs(HistoryType.SQUAD_CREATE);
            softly.assertThat(results.get(0).recordedAt()).isEqualTo(toDateTime);
            softly.assertThat(results.get(1).type()).isSameAs(HistoryType.SQUAD_CREATE);
            softly.assertThat(results.get(1).recordedAt()).isEqualTo(fromDateTime);
        });
    }
}
