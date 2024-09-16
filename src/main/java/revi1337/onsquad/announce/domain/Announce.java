package revi1337.onsquad.announce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.announce.domain.vo.Title;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Announce extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Column(nullable = false)
    private String content;

    @Column(name = "fixed", nullable = false)
    private boolean fixed = false;

    @Column(name = "fixed_at")
    private LocalDateTime fixedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_member_id", nullable = false)
    private CrewMember crewMember;

    @Builder
    private Announce(Long id, Title title, String content, Crew crew, CrewMember crewMember) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.crew = crew;
        this.crewMember = crewMember;
    }

    public void updateFixed(boolean fixed, LocalDateTime fixedAt) {
        this.fixed = fixed;
        this.fixedAt = fixedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announce announce)) return false;
        return id != null && Objects.equals(getId(), announce.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
