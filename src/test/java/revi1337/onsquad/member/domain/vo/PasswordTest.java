package revi1337.onsquad.member.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.error.exception.MemberDomainException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("Password vo 테스트")
class PasswordTest {

    private static final CharSequence BCRYPT_PASSWORD = "{bcrypt}$2a$10$RIWcAfMxZ1E9BsL60OcMVOiEsSumcCDqx0snDp6q8lNuxQI2XcSbi";
    private static final CharSequence INVALID_BCRYPT_PASSWORD = "{bcrypt}$2a$10$RIWcAfMxZ1E9BsL60OcMVOiEsSumcCDqx0snDp6q8lNuxQI2XcSbiTRASH";
    private static final String TEST_PASSWORD = "12345!@asa";

    @DisplayName("암호화된 Password 는 BCrypt 여야 한다.")
    @Test
    public void makeEncodedPassword() {
        // when
        Password password = new Password(BCRYPT_PASSWORD);

        // then
        assertThat(password).isNotNull();
    }

    @DisplayName("암호화된 Password 는 BCrypt 가 아니면 예외를 던진다.")
    @Test
    public void makeEncodedPassword2() {
        assertThatThrownBy(() -> new Password(INVALID_BCRYPT_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("암호화된 비밀번호는 bcrypt 포맷이어야 합니다.");
    }

    @DisplayName("Password 는 필수 사항이다.")
    @Test
    public void makePasswordWhenNull() {
        assertThatThrownBy(() -> new Password(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("비밀번호는 null 일 수 없습니다.");
    }

    @DisplayName("일반 Password 는 정규식이 통과되어야 한다.")
    @Test
    public void makePasswordWithFormat() {
        // when
        Password password = new Password(TEST_PASSWORD);

        // then
        assertThat(password).isNotNull();
    }

    @DisplayName("일반 Password 는 8자 미만이면 예외를 던진다.")
    @Test
    public void makePasswordWithFormat2() {
        // given
        String password = "15!@sa";

        // when & then
        assertSoftly(softly -> {
            softly.assertThat(password).hasSizeLessThan(8);
            softly.assertThatThrownBy(() -> new Password(password))
                    .isInstanceOf(MemberDomainException.InvalidPasswordFormat.class)
                    .hasMessage("비밀번호는 영문,숫자,특수문자 조합 8 ~ 20 길이어야 합니다.");
        });
    }

    @DisplayName("일반 Password 는 20 초과면 예외를 던진다.")
    @Test
    public void makePasswordWithFormat3() {
        // given
        String value = "1234a!@a1111111111111";

        // when & then
        assertSoftly(softly -> {
            softly.assertThat(value).hasSizeGreaterThan(20);
            softly.assertThatThrownBy(() -> new Password(value))
                    .isInstanceOf(MemberDomainException.InvalidPasswordFormat.class)
                    .hasMessage("비밀번호는 영문,숫자,특수문자 조합 8 ~ 20 길이어야 합니다.");
        });
    }

}