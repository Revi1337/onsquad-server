package revi1337.onsquad.announce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import revi1337.onsquad.announce.domain.vo.Title;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announce announce)) return false;
        return id != null && Objects.equals(id, announce.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
