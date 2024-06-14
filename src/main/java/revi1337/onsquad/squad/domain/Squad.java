package revi1337.onsquad.squad.domain;

import jakarta.persistence.*;
import lombok.*;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Categories;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.Objects;

import static jakarta.persistence.CascadeType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Squad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    @Embedded
    private Capacity capacity;

    @Embedded
    private Categories categories;

    @Embedded
    private Address address;

    private String kakaoLink;

    private String discordLink;

    @ManyToOne(fetch = FetchType.LAZY, cascade = PERSIST)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @Builder
    private Squad(Long id, Title title, Content content, Capacity capacity, Categories categories, Address address, String kakaoLink, String discordLink, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.categories = categories;
        this.address = address;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Squad squad)) return false;
        return id != null && Objects.equals(getId(), squad.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
