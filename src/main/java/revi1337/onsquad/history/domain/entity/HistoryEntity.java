package revi1337.onsquad.history.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import revi1337.onsquad.history.domain.HistoryType;

@NoArgsConstructor(access = PROTECTED)
@Table(name = "history")
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

    @Builder
    public HistoryEntity(Long memberId, Long crewId, Long squadId, Long squadCommentId, HistoryType type, String message) {
        this.memberId = memberId;
        this.crewId = crewId;
        this.squadId = squadId;
        this.squadCommentId = squadCommentId;
        this.type = type;
        this.message = message;
    }
}
