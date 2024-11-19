package revi1337.onsquad.member.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Address vo 테스트")
class AddressTest {

    @DisplayName("주소는 필수 명시사항이어야 한다.")
    @Test
    public void makeEmailWhenNull() {
        assertThatThrownBy(() -> new Address(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("주소는 null 일 수 없습니다.");
    }

}