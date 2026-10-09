package revi1337.onsquad.member.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.domain.error.MemberDomainException;

class NicknameTest {

    @Test
    @DisplayName("nickname 이 null 이면 실패한다.")
    void fail1() {
        String value = null;

        assertThatThrownBy(() -> new Nickname(value)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("nickname 의 길이가 2보다 짧으면 실패한다.")
    void fail2() {
        String value = "a";

        assertThatThrownBy(() -> new Nickname(value)).isInstanceOf(MemberDomainException.InvalidNicknameLength.class);
    }

    @Test
    @DisplayName("nickname 의 길이가 8보다 길면 실패한다.")
    void fail3() {
        String value = "aaaaaaaaa";

        assertThatThrownBy(() -> new Nickname(value)).isInstanceOf(MemberDomainException.InvalidNicknameLength.class);
    }

    @Test
    @DisplayName("랜덤 닉네임은 항상 닉네임 규칙(2~8자)을 만족하는 영문 소문자와 숫자 8자이다.")
    void random() {
        for (int i = 0; i < 1000; i++) {
            String value = Nickname.random().getValue();

            assertThat(value).hasSize(8).matches("[a-z0-9]{8}");
        }
    }

    @Test
    @DisplayName("랜덤 닉네임은 호출할 때마다 다른 값이 생성된다.")
    void random_generatesDifferentValues() {
        Set<String> values = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            values.add(Nickname.random().getValue());
        }

        assertThat(values.size()).isGreaterThan(90);
    }
}
