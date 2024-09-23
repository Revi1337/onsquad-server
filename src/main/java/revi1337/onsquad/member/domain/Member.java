package revi1337.onsquad.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.vo.*;

import java.util.*;

import static revi1337.onsquad.member.domain.vo.UserType.*;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    private static final Introduce DEFAULT_INTRODUCE = new Introduce("[소개 없음]");
    private static final String DEFAULT_KAKAO_LINK = "";
    private static final String DEFAULT_PROFILE_IMAGE = "";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Address address;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Password password;

    @Embedded
    private Introduce introduce = DEFAULT_INTRODUCE;

    private String profileImage = DEFAULT_PROFILE_IMAGE;

    private String kakaoLink = DEFAULT_KAKAO_LINK;

    @ColumnDefault("'GENERAL'")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType = GENERAL;

    @Enumerated(EnumType.STRING)
    private Mbti mbti;

    @OneToMany(mappedBy = "member")
    private final Set<Crew> crews = new HashSet<>();

    @Builder
    private Member(Long id, Email email, Address address, Nickname nickname, Password password, Introduce introduce, String profileImage, String kakaoLink, UserType userType, Mbti mbti) {
        this.id = id;
        this.email = email;
        this.address = address;
        this.nickname = nickname;
        this.password = password;
        this.introduce = introduce;
        this.profileImage = profileImage;
        this.kakaoLink = kakaoLink;
        this.userType = userType;
        this.mbti = mbti;
    }

    public void updateProfile(Member member) {
        this.nickname = member.getNickname();
        this.introduce = member.getIntroduce();
        this.mbti = member.getMbti();
        this.kakaoLink = member.getKakaoLink();
        this.address = member.getAddress();
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePassword(CharSequence encodedPassword) {
        this.password = password.update(encodedPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;
        return id != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}