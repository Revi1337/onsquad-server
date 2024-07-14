package revi1337.onsquad.crew.domain;

import jakarta.persistence.*;
import lombok.*;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;

import java.util.*;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.CascadeType.REFRESH;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "name", columnNames = "name")
        }
)
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Introduce introduce;

    @Embedded
    private Detail detail;

    @Embedded
    private HashTags hashTags;

    private String kakaoLink;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "crew", cascade = {PERSIST, MERGE, DETACH, REFRESH})
    private final List<CrewMember> crewMembers = new ArrayList<>();

    @Builder
    private Crew(Long id, Name name, Introduce introduce, Detail detail, HashTags hashTags, Image image, String kakaoLink, Member member) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.detail = detail;
        this.hashTags = hashTags;
        this.image = image;
        this.kakaoLink = kakaoLink;
        this.member = member;
    }

    public void addCrewMember(CrewMember... crewMembers) {
        for (CrewMember crewMember : crewMembers) {
            crewMember.addCrew(this);
            this.crewMembers.add(crewMember);
        }
    }

    public void updateCrew(String name, String introduce, String detail, Collection<String> hashTags, String kakaoLink, String imageUrl) {
        this.name = this.name.updateName(name);
        this.introduce = this.introduce.updateIntroduce(introduce);
        this.detail = this.detail.updateDetail(detail);
        this.hashTags = this.hashTags.updateHashTags(hashTags);
        this.kakaoLink = kakaoLink;
        this.image = this.image.updateImage(imageUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crew crew)) return false;
        return id != null && Objects.equals(getId(), crew.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
