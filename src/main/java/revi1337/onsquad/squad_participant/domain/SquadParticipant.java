package revi1337.onsquad.participant.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.squad.domain.Squad;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_squadparticipant_squad_crewmember", columnNames = {"squad_id", "crew_member_id"})
})
public class SquadParticipant extends RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id")
    private Squad squad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_member_id")
    private CrewMember crewMember;

    private SquadParticipant(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
        super(requestAt);
        this.squad = squad;
        this.crewMember = crewMember;
    }

    public static SquadParticipant of(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
        return new SquadParticipant(squad, crewMember, requestAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SquadParticipant that)) return false;
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
