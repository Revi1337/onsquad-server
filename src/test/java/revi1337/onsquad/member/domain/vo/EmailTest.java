package revi1337.onsquad.member.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.error.exception.MemberDomainException;

class EmailTest {

    @Test
    @DisplayName("이메일 객체 생성을 성공합니다.")
    void testEmail() {
        String value = "test@email.com";

        assertDoesNotThrow(() -> new Email(value));
    }

    @Test
    @DisplayName("이메일이 null 이면 실패합니다.")
    void testEmail2() {
        String value = null;

        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("이메일이 형식에 맞지 않으면 실패합니다.")
    void testEmail3() {
        String value = "email";

        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(MemberDomainException.InvalidEmailFormat.class);
    }
}