package revi1337.onsquad.crew_member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.vo.CrewRole;
import revi1337.onsquad.member.domain.Member;

import java.time.LocalDateTime;
import java.util.Objects;

import static revi1337.onsquad.crew_member.domain.vo.CrewRole.*;

@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_crewmember_crew_member", columnNames = {"crew_id", "member_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "requestAt", column = @Column(name = "participate_at", nullable = false))
})
public class CrewMember extends RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ColumnDefault("'GENERAL'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrewRole role;

    @Builder
    private CrewMember(Long id, Crew crew, Member member, CrewRole role, LocalDateTime participantAt) {
        super(participantAt);
        this.id = id;
        this.crew = crew;
        this.member = member;
        this.role = role == null ? GENERAL : role;
    }

    public static CrewMember forOwner(Member member, LocalDateTime participantAt) {
        return CrewMember.builder()
                .member(member)
                .role(OWNER)
                .participantAt(participantAt)
                .build();
    }

    public static CrewMember forGeneral(Crew crew, Member member, LocalDateTime participantAt) {
        return CrewMember.builder()
                .crew(crew)
                .member(member)
                .participantAt(participantAt)
                .build();
    }

    public static CrewMember of(Crew crew, Member member) {
        return CrewMember.builder()
                .crew(crew)
                .member(member)
                .build();
    }

    public void addCrew(Crew crew) {
        this.crew = crew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrewMember that)) return false;
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
