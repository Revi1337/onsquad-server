package revi1337.onsquad.member.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.error.exception.MemberDomainException;

@DisplayName("Email vo 테스트")
class EmailTest {

    @DisplayName("이메일은 필수 명시사항이어야 한다.")
    @Test
    public void makeEmailWhenNull() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("이메일은 null 일 수 없습니다.");
    }

    @DisplayName("정상적인 이메일은 Email 정규식이 통과되어야 한다.")
    @Test
    public void makeEmailWithFormat() {
        // given
        String value = "test@test.com";

        // when
        Email email = new Email(value);

        // then
        assertThat(email).isNotNull();
    }

    @DisplayName("정상적인 이메일은 Email 정규식이 통과되어야 한다.")
    @Test
    public void makeEmailWithFormat2() {
        // given
        String value = "test!!test.com.com";

        // when & then
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(MemberDomainException.InvalidEmailFormat.class)
                .hasMessage("이메일 형식이 올바르지 않습니다.");
    }

}