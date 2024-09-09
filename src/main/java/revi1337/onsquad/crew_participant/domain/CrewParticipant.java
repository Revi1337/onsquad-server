package revi1337.onsquad.participant.domain.crew_participant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_crewparticipant_crew_member", columnNames = {"crew_id", "member_id"})
})
public class CrewParticipant extends RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private CrewParticipant(Crew crew, Member member, LocalDateTime requestAt) {
        super(requestAt);
        this.crew = crew;
        this.member = member;
    }

    public static CrewParticipant of(Crew crew, Member member, LocalDateTime requestAt) {
        return new CrewParticipant(crew, member, requestAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrewParticipant that)) return false;
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
