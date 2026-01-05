package revi1337.onsquad.squad_member.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus.PENDING;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.GENERAL;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.LEADER;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus;
import revi1337.onsquad.squad_member.domain.entity.vo.SquadRole;

@DynamicInsert
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_squadmember_squad_member", columnNames = {"squad_id", "member_id"})})
@AttributeOverrides({@AttributeOverride(name = "requestAt", column = @Column(name = "participate_at", nullable = false))})
public class SquadMember extends RequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ColumnDefault("'GENERAL'")
    @Enumerated(STRING)
    @Column(nullable = false)
    private SquadRole role;

    @ColumnDefault("'PENDING'")
    @Enumerated(STRING)
    @Column(nullable = false)
    private JoinStatus status;

    SquadMember(SquadRole role, JoinStatus status, LocalDateTime participantAt) {
        super(participantAt);
        this.role = role == null ? GENERAL : role;
        this.status = status == null ? PENDING : status;
    }

    void addOwner(Member member) {
        this.member = member;
    }

    public void addSquad(Squad squad) {
        this.squad = squad;
    }

    public void leaveSquad() {
        squad.decreaseCurrentSize();
        releaseMember();
        releaseSquad();
    }

    public void promoteToLeader() {
        this.role = SquadRole.LEADER;
    }

    public void demoteToGeneral() {
        this.role = SquadRole.GENERAL;
    }

    public boolean matchSquadId(Long squadId) {
        return squad.matchId(squadId);
    }

    public boolean isNotLeader() {
        return !isLeader();
    }

    public boolean isLeader() {
        return this.role == LEADER;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SquadMember that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private void releaseMember() {
        this.member = null;
    }

    private void releaseSquad() {
        this.squad = null;
    }
}
