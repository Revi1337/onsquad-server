package revi1337.onsquad.crew_member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.vo.CrewRole;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.member.domain.Member;

import java.util.Objects;

import static revi1337.onsquad.crew_member.domain.vo.CrewRole.*;
import static revi1337.onsquad.crew_member.domain.vo.JoinStatus.*;

@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "request_at", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "participate_at", nullable = false))
})
public class CrewMember extends BaseEntity {

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

    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinStatus status;

    @Builder
    private CrewMember(Long id, Crew crew, Member member, JoinStatus status, CrewRole role) {
        this.id = id;
        this.crew = crew;
        this.member = member;
        this.status = status == null ? PENDING : status;
        this.role = role == null ? GENERAL : role;
    }

    public static CrewMember forOwner(Member member) {
        return CrewMember.builder()
                .member(member)
                .status(ACCEPT)
                .role(OWNER)
                .build();
    }

    public static CrewMember forGeneral(Crew crew, Member member) {
        return CrewMember.builder()
                .crew(crew)
                .member(member)
                .build();
    }

    public static CrewMember of(Crew crew, Member member, JoinStatus status) {
        return CrewMember.builder()
                .crew(crew)
                .member(member)
                .status(status)
                .build();
    }

    public void addCrew(Crew crew) {
        this.crew = crew;
    }

    public void updateStatus(JoinStatus status) {
        this.status = status;
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
