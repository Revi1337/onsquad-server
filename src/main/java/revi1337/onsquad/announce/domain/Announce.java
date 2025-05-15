package revi1337.onsquad.announce.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.announce.domain.vo.Title;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Announce extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Column(nullable = false)
    private String content;

    @Column(name = "fixed", nullable = false)
    private boolean fixed = false;

    @Column(name = "fixed_at")
    private LocalDateTime fixedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_member_id", nullable = false)
    private CrewMember crewMember;

    public Announce(String title, String content, Crew crew, CrewMember crewMember) {
        this.title = new Title(title);
        this.content = content;
        this.crew = crew;
        this.crewMember = crewMember;
    }

    public void update(String title, String content) {
        this.title = this.title.update(title);
        this.content = content;
    }

    public void fix(LocalDateTime fixedAt) {
        this.fixed = true;
        this.fixedAt = fixedAt;
    }

    public void unfix() {
        this.fixed = false;
        this.fixedAt = null;
    }

    public boolean isNotMatchWriterId(Long crewMemberId) {
        return !isMatchWriterId(crewMemberId);
    }

    public boolean isMatchWriterId(Long crewMemberId) {
        return crewMember.getId().equals(crewMemberId);
    }

    public boolean isNotFixed() {
        return !isFixed();
    }

    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Announce announce)) {
            return false;
        }
        return id != null && Objects.equals(getId(), announce.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
