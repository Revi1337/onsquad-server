package revi1337.onsquad.squad.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TitleTest {

    @Test
    @DisplayName("스쿼드 Title 값이 null 이면 실패한다.")
    void fail1() {
        assertThatThrownBy(() -> new Title(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", "타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 1"
    })
    @DisplayName("스쿼드 Title 값이 1 ~ 40 자가 아니면 실패한다.")
    void fail2(String value) {
        assertThatThrownBy(() -> new Title(value))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "타", "타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 타이틀 "
    })
    @DisplayName("스쿼드 Title 값이 1 ~ 40 자면 성공한다.")
    void success(String value) {
        Title title = new Title(value);

        assertThat(title).isEqualTo(new Title(value));
    }
}
