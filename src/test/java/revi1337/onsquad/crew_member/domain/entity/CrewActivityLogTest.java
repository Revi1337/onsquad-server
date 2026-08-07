package revi1337.onsquad.crew_member.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;

class CrewActivityLogTest {

    @Test
    @DisplayName("생성자로 전달한 값들이 그대로 필드에 반영된다")
    void constructor() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 6, 12, 0);

        CrewActivityLog activityLog = new CrewActivityLog(1L, 2L, CrewActivity.SQUAD_CREATE, CrewActivity.SQUAD_CREATE.getWeight(), createdAt);

        assertSoftly(softly -> {
            softly.assertThat(activityLog.getCrewId()).isEqualTo(1L);
            softly.assertThat(activityLog.getMemberId()).isEqualTo(2L);
            softly.assertThat(activityLog.getActivityType()).isSameAs(CrewActivity.SQUAD_CREATE);
            softly.assertThat(activityLog.getWeight()).isEqualTo(10);
            softly.assertThat(activityLog.getCreatedAt()).isEqualTo(createdAt);
        });
    }

    @Test
    @DisplayName("식별자(id)가 같으면 다른 필드 값이 달라도 동등한 엔티티로 취급한다")
    void equalsAndHashCode_sameId() {
        CrewActivityLog log1 = new CrewActivityLog(1L, 2L, CrewActivity.SQUAD_CREATE, 10, LocalDateTime.now());
        CrewActivityLog log2 = new CrewActivityLog(99L, 99L, CrewActivity.SQUAD_COMMENT, 1, LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(log1, "id", 1L);
        ReflectionTestUtils.setField(log2, "id", 1L);

        assertSoftly(softly -> {
            softly.assertThat(log1).isEqualTo(log2);
            softly.assertThat(log1.hashCode()).isEqualTo(log2.hashCode());
        });
    }

    @Test
    @DisplayName("식별자(id)가 다르면 다른 필드 값이 같아도 동등하지 않은 엔티티로 취급한다")
    void equalsAndHashCode_differentId() {
        LocalDateTime createdAt = LocalDateTime.now();
        CrewActivityLog log1 = new CrewActivityLog(1L, 2L, CrewActivity.SQUAD_CREATE, 10, createdAt);
        CrewActivityLog log2 = new CrewActivityLog(1L, 2L, CrewActivity.SQUAD_CREATE, 10, createdAt);
        ReflectionTestUtils.setField(log1, "id", 1L);
        ReflectionTestUtils.setField(log2, "id", 2L);

        assertThat(log1).isNotEqualTo(log2);
    }

    @Test
    @DisplayName("영속화되기 전(id가 null)인 두 엔티티는 서로 다른 인스턴스라면 동등하지 않다")
    void equals_transientEntitiesAreNotEqual() {
        CrewActivityLog log1 = new CrewActivityLog(1L, 2L, CrewActivity.SQUAD_CREATE, 10, LocalDateTime.now());
        CrewActivityLog log2 = new CrewActivityLog(1L, 2L, CrewActivity.SQUAD_CREATE, 10, LocalDateTime.now());

        assertThat(log1).isNotEqualTo(log2);
    }
}
