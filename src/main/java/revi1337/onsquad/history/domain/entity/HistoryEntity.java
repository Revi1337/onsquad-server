package revi1337.onsquad.history.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.history.domain.HistoryType;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "history", indexes = {@Index(name = "idx_history_member_recorded", columnList = "member_id, recorded_at")})
@Entity
public class HistoryEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long crewId;

    private Long squadId;

    private Long squadCommentId;

    @Enumerated(value = STRING)
    private HistoryType type;

    private String message;

    private LocalDateTime recordedAt;

    @Builder
    private HistoryEntity(Long memberId, Long crewId, Long squadId, Long squadCommentId, HistoryType type, String message, LocalDateTime recordedAt) {
        Objects.requireNonNull(memberId, "memberId cannot be null");
        Objects.requireNonNull(crewId, "crewId cannot be null");
        this.memberId = memberId;
        this.crewId = crewId;
        this.squadId = squadId;
        this.squadCommentId = squadCommentId;
        this.type = type;
        this.message = message;
        this.recordedAt = recordedAt == null ? LocalDateTime.now() : recordedAt;
    }
}
