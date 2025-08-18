package revi1337.onsquad.crew.domain;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtag;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.member.domain.Member;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "name", columnNames = "name")
        }
)
public class Crew extends BaseEntity {

    private static final int HASHTAG_BATCH_SIZE = 20;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Introduce introduce;

    @Embedded
    private Detail detail;

    private long currentSize;

    private String kakaoLink;

    private String imageUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OnDelete(action = CASCADE)
    @BatchSize(size = HASHTAG_BATCH_SIZE)
    @OneToMany(mappedBy = "crew")
    private final List<CrewHashtag> hashtags = new ArrayList<>();

    @OnDelete(action = CASCADE)
    @OneToMany(mappedBy = "crew", cascade = {PERSIST, MERGE})
    private final List<CrewMember> crewMembers = new ArrayList<>();

    @OnDelete(action = CASCADE)
    @OneToMany(mappedBy = "crew")
    private final List<CrewParticipant> participants = new ArrayList<>();

    public static Crew create(Member owner, String name, String introduce, String detail, String kakaoLink, String imageUrl) {
        Crew crew = new Crew(name, introduce, detail, kakaoLink, imageUrl);
        crew.registerOwner(owner);
        crew.addCrewMember(CrewMember.forOwner(owner, LocalDateTime.now()));
        return crew;
    }

    public static Crew of(String name, String introduce, String detail, String kakaoLink, String imageUrl) {
        return new Crew(name, introduce, detail, kakaoLink, imageUrl);
    }

    private Crew(String name, String introduce, String detail, String kakaoLink, String imageUrl) {
        this.name = new Name(name);
        this.introduce = new Introduce(introduce);
        this.detail = new Detail(detail);
        this.kakaoLink = kakaoLink;
        this.imageUrl = imageUrl;
    }

    public void addCrewMember(CrewMember... crewMembers) {
        for (CrewMember crewMember : crewMembers) {
            increaseSize();
            crewMember.addCrew(this);
            this.crewMembers.add(crewMember);
        }
    }

    private void increaseSize() {
        ++this.currentSize;
    }

    public void update(String name, String introduce, String detail, String kakaoLink) {
        this.name = this.name.updateName(name);
        this.introduce = this.introduce.updateIntroduce(introduce);
        this.detail = this.detail.updateDetail(detail);
        this.kakaoLink = kakaoLink;
    }

    public void updateImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
        }
    }

    public void deleteImage() {
        if (hasImage()) {
            this.imageUrl = null;
        }
    }

    public boolean isCreatedBy(Long memberId) {
        return member.getId().equals(memberId);
    }

    public boolean hasImage() {
        return !hasNotImage();
    }

    public boolean hasNotImage() {
        return imageUrl == null || imageUrl.isEmpty();
    }

    public long countMembers() {
        return currentSize;
    }

    public void registerOwner(Member owner) {
        this.member = owner;
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
}
