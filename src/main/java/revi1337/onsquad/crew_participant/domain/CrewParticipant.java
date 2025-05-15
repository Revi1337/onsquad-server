package revi1337.onsquad.crew_participant.domain;

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
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "crewparticipant_uidx_crew_id_member_id", columnNames = {"crew_id", "member_id"})
})
public class CrewParticipant extends RequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public CrewParticipant(Crew crew, Member member, LocalDateTime requestAt) {
        super(requestAt);
        this.crew = crew;
        this.member = member;
    }

    public CrewParticipant(Long id, Crew crew, Member member, LocalDateTime requestAt) {
        super(requestAt);
        this.id = id;
        this.crew = crew;
        this.member = member;
    }

    public boolean isNotFrom(Long crewId) {
        return !isFrom(crewId);
    }

    public boolean isFrom(Long crewId) {
        return crew.getId().equals(crewId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrewParticipant that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Long getRequestMemberId() {
        return member.getId();
    }
}
