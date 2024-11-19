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
import lombok.Builder;
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

    @Builder
    private SquadMember(Long id, Squad squad, CrewMember crewMember, SquadRole role, JoinStatus status,
                        LocalDateTime participantAt) {
        super(participantAt);
        this.id = id;
        this.squad = squad;
        this.crewMember = crewMember;
        this.role = role == null ? GENERAL : role;
        this.status = status == null ? PENDING : status;
    }

    public static SquadMember forLeader(CrewMember crewMember, LocalDateTime participantAt) {
        return SquadMember.builder()
                .crewMember(crewMember)
                .role(LEADER)
                .status(ACCEPT)
                .participantAt(participantAt)
                .build();
    }

    public static SquadMember forGeneral(CrewMember crewMember, LocalDateTime participantAt) {
        return SquadMember.builder()
                .crewMember(crewMember)
                .participantAt(participantAt)
                .build();
    }

    public void addSquad(Squad squad) {
        this.squad = squad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SquadMember that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
