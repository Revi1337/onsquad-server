package revi1337.onsquad.crew_member.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"crew_id", "member_id"}))
public class CrewActivityScore {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long crewId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private LocalDateTime lastActivityAt;

    public CrewActivityScore(Long crewId, Long memberId, int weight, LocalDateTime lastActivityAt) {
        this.crewId = crewId;
        this.memberId = memberId;
        this.weight = weight;
        this.lastActivityAt = lastActivityAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrewActivityScore that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
