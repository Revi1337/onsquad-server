package revi1337.onsquad.crew.domain;

import static jakarta.persistence.CascadeType.PERSIST;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtag;
import revi1337.onsquad.crew_member.domain.CrewMember;
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

    private String imageUrl;

    @BatchSize(size = HASHTAG_BATCH_SIZE)
    @OneToMany(mappedBy = "crew")
    private final List<CrewHashtag> hashtags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "crew", cascade = PERSIST)
    private final List<CrewMember> crewMembers = new ArrayList<>();

    @Builder
    private Crew(Long id, Name name, Introduce introduce, Detail detail, String imageUrl,
                 String kakaoLink, Member member) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.kakaoLink = kakaoLink;
        this.member = member;
    }

    public void addCrewMember(CrewMember... crewMembers) {
        for (CrewMember crewMember : crewMembers) {
            crewMember.addCrew(this);
            this.crewMembers.add(crewMember);
        }
    }

    public void update(String name, String introduce, String detail, String kakaoLink) {
        this.name = this.name.updateName(name);
        this.introduce = this.introduce.updateIntroduce(introduce);
        this.detail = this.detail.updateDetail(detail);
        this.kakaoLink = kakaoLink;
    }

    public boolean hasImage() {
        return !hasNotImage();
    }

    public boolean hasNotImage() {
        return imageUrl == null || imageUrl.isEmpty();
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void deleteImage() {
        this.imageUrl = null;
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

//package revi1337.onsquad.crew.domain;
//
//import static jakarta.persistence.CascadeType.PERSIST;
//import static jakarta.persistence.CascadeType.REMOVE;
//
//import jakarta.persistence.Embedded;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import jakarta.persistence.UniqueConstraint;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.BatchSize;
//import revi1337.onsquad.common.domain.BaseEntity;
//import revi1337.onsquad.crew.domain.vo.Detail;
//import revi1337.onsquad.crew.domain.vo.Introduce;
//import revi1337.onsquad.crew.domain.vo.Name;
//import revi1337.onsquad.crew_hashtag.domain.CrewHashtag;
//import revi1337.onsquad.crew_member.domain.CrewMember;
//import revi1337.onsquad.member.domain.Member;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Entity
//@Table(
//        uniqueConstraints = {
//                @UniqueConstraint(name = "name", columnNames = "name")
//        }
//)
//public class Crew extends BaseEntity {
//
//    private static final int HASHTAG_BATCH_SIZE = 20;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Embedded
//    private Name name;
//
//    @Embedded
//    private Introduce introduce;
//
//    @Embedded
//    private Detail detail;
//
//    private String kakaoLink;
//
//    private String imageUrl;
//
//    @BatchSize(size = HASHTAG_BATCH_SIZE)
//    @OneToMany(mappedBy = "crew", cascade = REMOVE, orphanRemoval = true)
//    private final List<CrewHashtag> hashtags = new ArrayList<>();
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;
//
//    @OneToMany(mappedBy = "crew", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
//    private final List<CrewMember> crewMembers = new ArrayList<>();
//
//    @Builder
//    private Crew(Long id, Name name, Introduce introduce, Detail detail, String imageUrl,
//                 String kakaoLink, Member member) {
//        this.id = id;
//        this.name = name;
//        this.introduce = introduce;
//        this.detail = detail;
//        this.imageUrl = imageUrl;
//        this.kakaoLink = kakaoLink;
//        this.member = member;
//    }
//
//    public void addCrewMember(CrewMember... crewMembers) {
//        for (CrewMember crewMember : crewMembers) {
//            crewMember.addCrew(this);
//            this.crewMembers.add(crewMember);
//        }
//    }
//
//    public void update(String name, String introduce, String detail, String kakaoLink) {
//        this.name = this.name.updateName(name);
//        this.introduce = this.introduce.updateIntroduce(introduce);
//        this.detail = this.detail.updateDetail(detail);
//        this.kakaoLink = kakaoLink;
//    }
//
//    public boolean hasImage() {
//        return !hasNotImage();
//    }
//
//    public boolean hasNotImage() {
//        return imageUrl == null || imageUrl.isEmpty();
//    }
//
//    public void updateImage(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public void deleteImage() {
//        this.imageUrl = null;
//    }
//
//    public void releaseAssociations() {
//        releaseHashtags();
//        releaseMembers();
//    }
//
//    private void releaseHashtags() {
//        hashtags.forEach(CrewHashtag::releaseCrew);
//        hashtags.clear();
//    }
//
//    private void releaseMembers() {
//        crewMembers.forEach(CrewMember::releaseCrew);
//        crewMembers.clear();
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof Crew crew)) {
//            return false;
//        }
//        return id != null && Objects.equals(getId(), crew.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(getId());
//    }
//
//    public boolean isCreatedByOwnerUsing(Long memberId) {
//        return member.getId().equals(memberId);
//    }
//}
