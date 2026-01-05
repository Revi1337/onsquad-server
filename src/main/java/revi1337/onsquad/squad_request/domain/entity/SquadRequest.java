package revi1337.onsquad.squad_request.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_squadrequest_squad_member", columnNames = {"squad_id", "member_id"})})
public class SquadRequest extends RequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private SquadRequest(Squad squad, Member member, LocalDateTime requestAt) {
        super(requestAt);
        this.squad = squad;
        this.member = member;
    }

    public static SquadRequest of(Squad squad, Member member, LocalDateTime requestAt) {
        return new SquadRequest(squad, member, requestAt);
    }

    public Long getRequesterId() {
        return member.getId();
    }

    public Long getSquadId() {
        return squad.getId();
    }

    public boolean matchSquadId(Long squadId) {
        return squad.matchId(squadId);
    }

    public boolean mismatchSquadId(Long squadId) {
        return !matchSquadId(squadId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SquadRequest that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
