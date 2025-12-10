package revi1337.onsquad.member.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.member.domain.entity.vo.Address;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Introduce;
import revi1337.onsquad.member.domain.entity.vo.Mbti;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.entity.vo.Password;
import revi1337.onsquad.member.domain.entity.vo.UserType;

@DynamicUpdate
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_member_email", columnNames = "email")})
@Entity
public class Member extends BaseEntity {

    public static final Introduce DEFAULT_INTRODUCE = Introduce.defaultValue();
    public static final Address DEFAULT_ADDRESS = Address.defaultValue();
    public static final UserType DEFAULT_USER_TYPE = UserType.GENERAL;
    public static final String DEFAULT_KAKAO_LINK = "";
    public static final String DEFAULT_IMAGE = "https://d3jao8gvkosd1k.cloudfront.net/onsquad/default/member-default.svg";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Introduce introduce;

    @Embedded
    private Address address;

    private String profileImage;

    private String kakaoLink;

    @Enumerated(STRING)
    @Column(nullable = false)
    private UserType userType;

    @Enumerated(STRING)
    private Mbti mbti;

    public static Member general(String email, String password, String nickname, String address, String addressDetail) {
        return Member.builder()
                .email(new Email(email))
                .password(Password.raw(password))
                .nickname(new Nickname(nickname))
                .address(new Address(address, addressDetail))
                .build();
    }

    public static Member oauth2(String email, String password, String nickname, String image, UserType userType) {
        return Member.builder()
                .email(new Email(email))
                .password(Password.encrypted(password))
                .nickname(new Nickname(nickname))
                .image(image)
                .userType(userType)
                .build();
    }

    /**
     * A builder that incrementally initializes fields to construct a {@link Member} object. It is intended for use within the {@code Member} class via factory
     * methods, and for testing purposes only. Usage for any other purposes is not recommended.
     */
    @Builder(access = PUBLIC)
    private Member(Email email, Password password, Nickname nickname, Introduce introduce, Address address,
                   String image, String kakaoLink, UserType userType, Mbti mbti) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.introduce = introduce == null ? DEFAULT_INTRODUCE : introduce;
        this.address = address == null ? DEFAULT_ADDRESS : address;
        this.profileImage = image == null ? DEFAULT_IMAGE : image;
        this.kakaoLink = kakaoLink == null ? DEFAULT_KAKAO_LINK : kakaoLink;
        this.userType = userType == null ? DEFAULT_USER_TYPE : userType;
        this.mbti = mbti;
    }

    public void updateProfile(MemberBase memberBase) {
        this.nickname = this.nickname.update(memberBase.nickname());
        this.introduce = this.introduce.update(memberBase.introduce());
        this.mbti = Mbti.parse(memberBase.mbti());
        this.kakaoLink = memberBase.kakaoLink();
        this.address = this.address.update(memberBase.address(), memberBase.addressDetail());
    }

    public void updateImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void changeDefaultImage() {
        this.profileImage = DEFAULT_IMAGE;
    }

    public void updatePassword(String password) {
        this.password = this.password.update(password);
    }

    public boolean matchId(Long memberId) {
        return id.equals(memberId);
    }

    public boolean hasNotDefaultImage() {
        return !hasDefaultImage();
    }

    public boolean hasDefaultImage() {
        return DEFAULT_IMAGE.equals(profileImage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member member)) {
            return false;
        }
        return id != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public record MemberBase(
            String nickname,
            String introduce,
            String mbti,
            String address,
            String addressDetail,
            String kakaoLink
    ) {

    }
}
