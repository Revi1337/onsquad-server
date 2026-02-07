package revi1337.onsquad.history.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.history.domain.HistoryType;

class HistoryEntityTest {

    @Test
    @DisplayName("history 객체 생성에 성공한다.")
    void constructor() {
        LocalDateTime recordedAt = LocalDateTime.of(2026, 1, 4, 17, 20);

        HistoryEntity historyEntity = HistoryEntity.builder()
                .memberId(1L)
                .crewId(2L)
                .type(HistoryType.CREW_CREATE)
                .message("created-crew")
                .recordedAt(recordedAt)
                .build();

        assertThat(historyEntity.getRecordedAt()).isEqualTo(recordedAt);
    }

    @Test
    @DisplayName("recordedAt 필드를 적지 않아도 내부에서 시간이 측정된다.")
    void constructor2() {
        HistoryEntity historyEntity = HistoryEntity.builder()
                .memberId(1L)
                .crewId(2L)
                .type(HistoryType.CREW_CREATE)
                .message("created-crew")
                .build();

        assertThat(historyEntity.getRecordedAt()).isNotNull();
    }

    @Test
    @DisplayName("history 의 memberId 가 빠지면 NPE 가 발생한다.")
    void constructorFail1() {
        LocalDateTime recordedAt = LocalDateTime.of(2026, 1, 4, 17, 20);

        assertThatNullPointerException().isThrownBy(() -> HistoryEntity.builder()
                .crewId(2L)
                .type(HistoryType.CREW_CREATE)
                .message("message")
                .recordedAt(recordedAt)
                .build()
        ).withMessage("memberId cannot be null");
    }

    @Test
    @DisplayName("history 의 crewId 가 빠지면 NPE 가 발생한다.")
    void constructorFail2() {
        LocalDateTime recordedAt = LocalDateTime.of(2026, 1, 4, 17, 20);

        assertThatNullPointerException().isThrownBy(() -> HistoryEntity.builder()
                .memberId(1L)
                .type(HistoryType.CREW_CREATE)
                .message("message")
                .recordedAt(recordedAt)
                .build()
        ).withMessage("crewId cannot be null");
    }

    @Test
    @DisplayName("history 의 type 이 빠지면 NPE 가 발생한다.")
    void constructorFail3() {
        LocalDateTime recordedAt = LocalDateTime.of(2026, 1, 4, 17, 20);

        assertThatNullPointerException().isThrownBy(() -> HistoryEntity.builder()
                .memberId(1L)
                .crewId(2L)
                .message("message")
                .recordedAt(recordedAt)
                .build()
        ).withMessage("type cannot be null");
    }

    @Test
    @DisplayName("history 의 message 가 빠지면 NPE 가 발생한다.")
    void constructorFail4() {
        LocalDateTime recordedAt = LocalDateTime.of(2026, 1, 4, 17, 20);

        assertThatNullPointerException().isThrownBy(() -> HistoryEntity.builder()
                .memberId(1L)
                .crewId(2L)
                .type(HistoryType.CREW_CREATE)
                .recordedAt(recordedAt)
                .build()
        ).withMessage("message cannot be null");
    }
}
