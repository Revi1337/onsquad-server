package revi1337.onsquad.member.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.error.exception.MemberDomainException;

@DisplayName("Nickname vo 테스트")
class NicknameTest {

    @DisplayName("닉네임은 필수 명시사항이어야 한다.")
    @Test
    public void makeNicknameWhenNull() {
        assertThatThrownBy(() -> new Nickname(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("닉네임은 null 일 수 없습니다.");
    }

    @DisplayName("닉네임은 2자 이상 8자이하여야 한다.")
    @Test
    public void makeNicknameWithLength() {
        // given
        String value = "12";

        // when
        Nickname nickname = new Nickname(value);

        // then
        assertThat(nickname).isNotNull();
    }

    @DisplayName("닉네임은 2자 미만이면 안된다.")
    @Test
    public void makeNicknameWithLength2() {
        // given
        String value = "1";

        // when & then
        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(MemberDomainException.InvalidNicknameLength.class)
                .hasMessage("닉네임은 2 자 이상 8 자 이하여야합니다.");
    }

    @DisplayName("닉네임은 8자 초과이면 안된다.")
    @Test
    public void makeNicknameWithLength3() {
        // given
        String value = "123456789";

        // when & then
        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(MemberDomainException.InvalidNicknameLength.class)
                .hasMessage("닉네임은 2 자 이상 8 자 이하여야합니다.");
    }

}