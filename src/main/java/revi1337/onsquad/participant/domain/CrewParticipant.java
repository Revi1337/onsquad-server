package revi1337.onsquad.participant.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.squad.domain.Squad;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewParticipant extends Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private CrewParticipant(Crew crew, Member member, LocalDateTime time) {
        super(time);
        this.crew = crew;
        this.member = member;
    }

    public static CrewParticipant of(Crew crew, Member member, LocalDateTime time) {
        return new CrewParticipant(crew, member, time);
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
