package revi1337.onsquad.squad_member.domain;

import static revi1337.onsquad.crew_member.domain.vo.JoinStatus.ACCEPT;
import static revi1337.onsquad.crew_member.domain.vo.JoinStatus.PENDING;
import static revi1337.onsquad.squad_member.domain.vo.SquadRole.GENERAL;
import static revi1337.onsquad.squad_member.domain.vo.SquadRole.LEADER;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad_member.domain.vo.SquadRole;

@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_squadmember_squad_crewmember", columnNames = {"squad_id", "crew_member_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "requestAt", column = @Column(name = "participate_at", nullable = false))
})
public class SquadMember extends RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_member_id", nullable = false)
    private CrewMember crewMember;

    @ColumnDefault("'GENERAL'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SquadRole role;

    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinStatus status;

    public SquadMember(SquadRole role, JoinStatus status, LocalDateTime participantAt) {
        super(participantAt);
        this.role = role == null ? GENERAL : role;
        this.status = status == null ? PENDING : status;
    }

    public static SquadMember forLeader(Squad squad, CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = forLeader(crewMember, participantAt);
        squadMember.addSquad(squad);
        return squadMember;
    }

    public static SquadMember forGeneral(Squad squad, CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = forGeneral(crewMember, participantAt);
        squadMember.addSquad(squad);
        return squadMember;
    }

    public static SquadMember forLeader(CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = new SquadMember(LEADER, ACCEPT, participantAt);
        squadMember.addOwner(crewMember);
        return squadMember;
    }

    public static SquadMember forGeneral(CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = new SquadMember(GENERAL, PENDING, participantAt);
        squadMember.addOwner(crewMember);
        return squadMember;
    }

    public boolean matchCrewMemberId(Long crewMemberId) {
        return crewMember.getId().equals(crewMemberId);
    }

    public boolean doesNotMatchCrewMemberId(Long crewMemberId) {
        return !matchCrewMemberId(crewMemberId);
    }

    public boolean isNotLeader() {
        return !isLeader();
    }

    public boolean isLeader() {
        return this.role == LEADER;
    }

    private void addOwner(CrewMember crewMember) {
        this.crewMember = crewMember;
    }

    public void addSquad(Squad squad) {
        this.squad = squad;
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

    public boolean isSameCrewMemberId(Long crewMemberId) {
        return crewMember.getId().equals(crewMemberId);
    }
}
