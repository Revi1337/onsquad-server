package revi1337.onsquad.crew.domain;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtag;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "name", columnNames = "name")
        }
)
public class Crew extends BaseEntity {

    private static final int HASHTAG_BATCH_SIZE = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Introduce introduce;

    @Embedded
    private Detail detail;

    private String kakaoLink;

    @BatchSize(size = HASHTAG_BATCH_SIZE)
    @OneToMany(mappedBy = "crew")
    private final List<CrewHashtag> hashtags = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "crew", cascade = {PERSIST, MERGE, DETACH, REFRESH})
    private final List<CrewMember> crewMembers = new ArrayList<>();

    @Builder
    private Crew(Long id, Name name, Introduce introduce, Detail detail, HashTags hashTags, Image image,
                 String kakaoLink, Member member) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.detail = detail;
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

    public void updateCrew(String name, String introduce, String detail, String kakaoLink) {
        this.name = this.name.updateName(name);
        this.introduce = this.introduce.updateIntroduce(introduce);
        this.detail = this.detail.updateDetail(detail);
        this.kakaoLink = kakaoLink;
    }

    public boolean hasNotImage() {
        return image == null;
    }

    public void updateImage(String imageUrl) {
        if (hasNotImage()) {
            this.image = new Image(imageUrl);
        }
        this.image = this.image.updateImage(imageUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Crew crew)) {
            return false;
        }
        return id != null && Objects.equals(getId(), crew.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public boolean isCreatedByOwnerUsing(Long memberId) {
        return member.getId().equals(memberId);
    }
}
