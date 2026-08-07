package revi1337.onsquad.crew_member.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class CrewActivityLog {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long crewId;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(STRING)
    @Column(nullable = false)
    private CrewActivity activityType;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CrewActivityLog(Long crewId, Long memberId, CrewActivity activityType, int weight, LocalDateTime createdAt) {
        this.crewId = crewId;
        this.memberId = memberId;
        this.activityType = activityType;
        this.weight = weight;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrewActivityLog that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
