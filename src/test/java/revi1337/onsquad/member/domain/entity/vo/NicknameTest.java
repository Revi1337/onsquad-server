package revi1337.onsquad.member.domain.entity.vo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.error.exception.MemberDomainException;

class NicknameTest {

    @Test
    @DisplayName("nickname 이 null 이면 실패합니다.")
    void testNickname1() {
        String value = null;

        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("nickname 의 길이가 2보다 짧으면 실패합니다.")
    void testNickname2() {
        String value = "a";

        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(MemberDomainException.InvalidNicknameLength.class);
    }

    @Test
    @DisplayName("nickname 의 길이가 8보다 길면 실패합니다.")
    void testNickname3() {
        String value = "aaaaaaaaa";

        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(MemberDomainException.InvalidNicknameLength.class);
    }
}
