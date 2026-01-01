package revi1337.onsquad.crew_member.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static revi1337.onsquad.crew_member.domain.entity.vo.CrewRole.GENERAL;
import static revi1337.onsquad.crew_member.domain.entity.vo.CrewRole.MANAGER;
import static revi1337.onsquad.crew_member.domain.entity.vo.CrewRole.OWNER;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.vo.CrewRole;
import revi1337.onsquad.member.domain.entity.Member;

@DynamicInsert
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(name = "uk_crewmember_squad_member", columnNames = {"crew_id", "member_id"}),
        indexes = {@Index(name = "idx_crewmember_participate_at", columnList = "participate_at")}
)
@AttributeOverrides({@AttributeOverride(name = "requestAt", column = @Column(name = "participate_at", nullable = false))})
public class CrewMember extends RequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ColumnDefault("'GENERAL'")
    @Enumerated(STRING)
    @Column(nullable = false)
    private CrewRole role = GENERAL;

    CrewMember(Member member, CrewRole role, LocalDateTime participantAt) {
        this(null, member, role, participantAt);
    }

    CrewMember(Crew crew, Member member, CrewRole role, LocalDateTime participantAt) {
        super(participantAt);
        this.crew = crew;
        this.member = member;
        this.role = role == null ? GENERAL : role;
    }

    public void addCrew(Crew crew) {
        this.crew = crew;
    }

    public void releaseCrew() {
        this.crew = null;
    }

    public boolean isNotOwner() {
        return !isOwner();
    }

    public boolean isOwner() {
        return role == OWNER;
    }

    public boolean isManager() {
        return role == MANAGER;
    }

    public boolean isGeneral() {
        return role == GENERAL;
    }

    public boolean isLessThenManager() {
        return !isGreaterThenManager();
    }

    public Long getActualMemberId() {
        return member.getId();
    }

    public boolean isGreaterThenManager() {
        return role == OWNER || role == MANAGER;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CrewMember that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
