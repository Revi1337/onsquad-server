package revi1337.onsquad.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_OAUTH_PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_UUID_PASSWORD_VALUE;

import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;
import revi1337.onsquad.member.domain.Member.MemberBase;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.member.domain.vo.UserType;

class MemberTest {

    @Test
    @DisplayName("일반 사용자 생성에 성공한다.")
    void testMember1() {
        Member revi = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );

        assertAll(() -> {
            assertThat(revi.getEmail()).isEqualTo(new Email(REVI_EMAIL_VALUE));
            assertThat(revi.getPassword()).isEqualTo(Password.raw(REVI_PASSWORD_VALUE));
            assertThat(revi.getAddress()).isEqualTo(new Address(REVI_ADDRESS_VALUE, REVI_ADDRESS_DETAIL_VALUE));
            assertThat(revi.getNickname()).isEqualTo(new Nickname(REVI_NICKNAME_VALUE));

            assertThat(revi.getIntroduce()).isEqualTo(Member.DEFAULT_INTRODUCE);
            assertThat(revi.getProfileImage()).isEqualTo(Member.DEFAULT_IMAGE);
            assertThat(revi.getKakaoLink()).isEqualTo(Member.DEFAULT_KAKAO_LINK);
            assertThat(revi.getUserType()).isSameAs(Member.DEFAULT_USER_TYPE);
            assertThat(revi.getMbti()).isNull();
        });
    }

    @Test
    @DisplayName("OAuth2 사용자 생성에 성공한다.")
    void testMember2() {
        Member revi = Member.oauth2(
                REVI_EMAIL_VALUE,
                REVI_UUID_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_OAUTH_PROFILE_IMAGE_LINK,
                UserType.KAKAO
        );

        assertAll(() -> {
            assertThat(revi.getEmail()).isEqualTo(new Email(REVI_EMAIL_VALUE));
            assertThat(revi.getPassword()).isEqualTo(Password.uuid(REVI_UUID_PASSWORD_VALUE));
            assertThat(revi.getNickname()).isEqualTo(new Nickname(REVI_NICKNAME_VALUE));
            assertThat(revi.getProfileImage()).isEqualTo(REVI_OAUTH_PROFILE_IMAGE_LINK);

            assertThat(revi.getAddress()).isEqualTo(Member.DEFAULT_ADDRESS);
            assertThat(revi.getIntroduce()).isEqualTo(Member.DEFAULT_INTRODUCE);
            assertThat(revi.getKakaoLink()).isEqualTo(Member.DEFAULT_KAKAO_LINK);
            assertThat(revi.getUserType()).isSameAs(UserType.KAKAO);
            assertThat(revi.getMbti()).isNull();
        });
    }

    @Test
    @DisplayName("회원정보 프로필 업데이트에 성공한다.")
    void updateProfile() {
        Member revi = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        MemberBase andongBase = new MemberBase(
                ANDONG_NICKNAME_VALUE,
                ANDONG_INTRODUCE_VALUE,
                ANDONG_MBTI_VALUE,
                ANDONG_ADDRESS_VALUE,
                ANDONG_ADDRESS_DETAIL_VALUE,
                ANDONG_KAKAO_LINK
        );

        revi.updateProfile(andongBase);

        assertAll(() -> {
            assertThat(revi.getNickname()).isEqualTo(new Nickname(ANDONG_NICKNAME_VALUE));
            assertThat(revi.getIntroduce()).isEqualTo(new Introduce(ANDONG_INTRODUCE_VALUE));
            assertThat(revi.getMbti()).isSameAs(Mbti.ISFP);
            assertThat(revi.getAddress()).isEqualTo(new Address(ANDONG_ADDRESS_VALUE, ANDONG_ADDRESS_DETAIL_VALUE));
            assertThat(revi.getKakaoLink()).isEqualTo(ANDONG_KAKAO_LINK);

            assertThat(revi.getEmail()).isEqualTo(new Email(REVI_EMAIL_VALUE));
            assertThat(revi.getPassword()).isEqualTo(Password.raw(REVI_PASSWORD_VALUE));
        });
    }

    @Test
    @DisplayName("프로필 이미지 정보를 업데이트에 성공한다.")
    void updateImage() {
        Member revi = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        String changedImage = "https://changeed_img.com";

        revi.updateImage(changedImage);

        assertThat(revi.getProfileImage()).isEqualTo(changedImage);
    }

    @Test
    @DisplayName("기본 프로필 이미지 변경에 성공한다.")
    void changeDefaultImage() {
        Member revi = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        String changedImage = "https://changeed_img.com";
        revi.updateImage(changedImage);

        revi.changeDefaultImage();

        assertThat(revi.hasDefaultImage()).isTrue();
    }

    @Test
    @DisplayName("기본 프로필 이미를 갖고있는지 검증에 성공한다.")
    void hasDefaultImage() {
        Member revi = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );

        assertThat(revi.hasDefaultImage()).isTrue();
    }

    @Test
    @DisplayName("기본 프로필 이미지를 제외한 이미지를 갖고있는지 검증에 성공한다.")
    void hasNotDefaultImage() {
        Member revi = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        String changedImage = "https://changeed_img.com";
        revi.updateImage(changedImage);

        assertThat(revi.hasNotDefaultImage()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경에 성공한다.")
    void updatePassword() {
        Member revi = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        String changedPassword = "12345!@asb";

        revi.updatePassword(changedPassword);

        assertThat(revi.getPassword()).isEqualTo(Password.raw(changedPassword));
    }

    @Test
    @DisplayName("회원이 동등성 검사에 성공한다. (1)")
    void equalsSuccess() throws NoSuchFieldException {
        // given
        Member revi1 = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        Field idField1 = revi1.getClass().getDeclaredField("id");
        idField1.setAccessible(true);
        ReflectionUtils.setField(idField1, revi1, 1L);

        Member revi2 = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        Field idField2 = revi2.getClass().getDeclaredField("id");
        idField2.setAccessible(true);
        ReflectionUtils.setField(idField2, revi2, 1L);

        // when & then
        assertThat(revi1).isEqualTo(revi2);
    }

    @Test
    @DisplayName("회원이 동등성 검사에 성공한다. (2)")
    void equalsFail() throws NoSuchFieldException {
        // given
        Member revi1 = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        Field idField1 = revi1.getClass().getDeclaredField("id");
        idField1.setAccessible(true);
        ReflectionUtils.setField(idField1, revi1, 1L);

        Member revi2 = Member.general(
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                REVI_NICKNAME_VALUE,
                REVI_ADDRESS_VALUE,
                REVI_ADDRESS_DETAIL_VALUE
        );
        Field idField2 = revi2.getClass().getDeclaredField("id");
        idField2.setAccessible(true);
        ReflectionUtils.setField(idField2, revi2, 2L);

        // when & then
        assertThat(revi1).isNotEqualTo(revi2);
    }
}
