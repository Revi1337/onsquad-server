package revi1337.onsquad.announce.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

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
import org.hibernate.annotations.OnDelete;
import revi1337.onsquad.announce.domain.entity.vo.Title;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

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

    @OnDelete(action = CASCADE)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public Announce(String title, String content, Crew crew, Member member) {
        this.title = new Title(title);
        this.content = content;
        this.crew = crew;
        this.member = member;
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

    public boolean mismatchCrewId(Long crewId) {
        return !crew.matchId(crewId);
    }

    public boolean mismatchMemberId(Long memberId) {
        return !matchMemberId(memberId);
    }

    public boolean matchMemberId(Long memberId) {
        return member.matchId(memberId);
    }

    public boolean isUnfixed() {
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
