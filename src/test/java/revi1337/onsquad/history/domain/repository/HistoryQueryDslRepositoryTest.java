package revi1337.onsquad.history.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewCreateHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createSquadCreateHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

@Import(HistoryQueryDslRepository.class)
class HistoryQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private HistoryJpaRepository historyJpaRepository;

    @Autowired
    private HistoryQueryDslRepository historyQueryDslRepository;

    @Test
    @DisplayName("지정한 날짜 범위의 시작 시각(00:00:00)부터 종료 날짜의 마지막 시각(23:59:59)까지의 데이터를 모두 조회한다")
    void findAllByMemberIdAndDateRangeBoundary1() {
        LocalDate baseRecordedAt = LocalDate.of(2026, 1, 4);
        LocalDateTime fromDateTime = baseRecordedAt.atStartOfDay();
        LocalDateTime toDateTime = baseRecordedAt.plusDays(6).atTime(23, 59, 59);
        historyJpaRepository.save(createCrewCreateHistory(2L, 3L, "crew-name-3", fromDateTime));
        historyJpaRepository.save(createCrewCreateHistory(2L, 4L, "crew-name-4", toDateTime));
        clearPersistenceContext();
        PageRequest pageable = PageRequest.of(0, 2, Sort.by("recordedAt").descending());

        Page<HistoryEntity> histories = historyQueryDslRepository.findAllByMemberIdAndDateRange(
                2L,
                baseRecordedAt,
                baseRecordedAt.plusDays(6),
                HistoryType.CREW_CREATE,
                pageable
        );

        assertSoftly(softly -> {
            List<HistoryEntity> contents = histories.getContent();
            softly.assertThat(contents.size()).isEqualTo(2);
            softly.assertThat(contents.get(0).getRecordedAt()).isEqualTo(toDateTime);
            softly.assertThat(contents.get(1).getRecordedAt()).isEqualTo(fromDateTime);
        });
    }

    @Test
    @DisplayName("조회 종료 날짜의 다음 날 00:00:00에 기록된 데이터는 조회 결과에서 제외한다")
    void findAllByMemberIdAndDateRangeBoundary2() {
        LocalDate baseRecordedAt = LocalDate.of(2026, 1, 4);
        LocalDateTime fromDateTime = baseRecordedAt.atStartOfDay();
        LocalDateTime toDateTime = baseRecordedAt.plusDays(7).atStartOfDay();
        historyJpaRepository.save(createCrewCreateHistory(2L, 3L, "crew-name-3", fromDateTime));
        historyJpaRepository.save(createCrewCreateHistory(2L, 4L, "crew-name-4", toDateTime));
        clearPersistenceContext();
        PageRequest pageable = PageRequest.of(0, 2, Sort.by("recordedAt").descending());

        Page<HistoryEntity> histories = historyQueryDslRepository.findAllByMemberIdAndDateRange(
                2L,
                baseRecordedAt,
                baseRecordedAt.plusDays(6),
                HistoryType.CREW_CREATE,
                pageable
        );

        assertSoftly(softly -> {
            List<HistoryEntity> contents = histories.getContent();
            softly.assertThat(contents.size()).isEqualTo(1);
            softly.assertThat(contents.get(0).getRecordedAt()).isEqualTo(fromDateTime);
        });
    }

    @Test
    @DisplayName("활동 유형(HistoryType)이 주어지지 않으면, 유형에 상관없이 해당 기간 내의 모든 활동 이력을 조회한다")
    void findAllByMemberIdAndDateRangeTypeNull() {
        LocalDate baseRecordedAt = LocalDate.of(2026, 1, 4);
        LocalDateTime fromDateTime = baseRecordedAt.atStartOfDay();
        LocalDateTime toDateTime = baseRecordedAt.plusDays(6).atTime(23, 59, 59);
        historyJpaRepository.save(createCrewCreateHistory(2L, 3L, "crew-name-3", fromDateTime));
        historyJpaRepository.save(createCrewCreateHistory(2L, 4L, "crew-name-4", toDateTime));
        historyJpaRepository.save(createSquadCreateHistory(2L, 4L, "crew-name-4", 1L, "squad-name-1", fromDateTime));
        historyJpaRepository.save(createSquadCreateHistory(2L, 4L, "crew-name-4", 2L, "squad-name-2", toDateTime));
        clearPersistenceContext();
        PageRequest pageable = PageRequest.of(0, 4, Sort.by("recordedAt").descending());

        Page<HistoryEntity> histories = historyQueryDslRepository.findAllByMemberIdAndDateRange(
                2L,
                baseRecordedAt,
                baseRecordedAt.plusDays(6),
                null,
                pageable
        );

        assertSoftly(softly -> {
            List<HistoryEntity> contents = histories.getContent();
            softly.assertThat(contents.size()).isEqualTo(4);
            softly.assertThat(contents)
                    .extracting(HistoryEntity::getRecordedAt)
                    .isSortedAccordingTo(Comparator.reverseOrder());
        });
    }
}
