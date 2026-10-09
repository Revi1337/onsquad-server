package revi1337.onsquad.member.domain.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    @DisplayName("기본 주소는 주소와 상세주소 모두 빈 문자열이다.")
    void defaultValue() {
        Address address = Address.defaultValue();

        assertSoftly(softly -> {
            softly.assertThat(address.getValue()).isEmpty();
            softly.assertThat(address.getDetail()).isEmpty();
        });
    }

    @Test
    @DisplayName("주소 혹은 상세주소가 null 이면 실패한다.")
    void constructorFail() {
        String address = null;
        String addressDetail = null;

        assertThatThrownBy(() -> new Address(address, addressDetail)).isInstanceOf(IllegalArgumentException.class);
    }

}
