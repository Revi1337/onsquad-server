package revi1337.onsquad.auth.oauth.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import revi1337.onsquad.auth.oauth.application.OAuth2Vendor;

class OAuth2VendorConverterTest {

    private final OAuth2VendorConverter converter = new OAuth2VendorConverter();

    @ParameterizedTest
    @ValueSource(strings = {"kakao", "KAKAO", "Kakao", " kakao "})
    @DisplayName("대소문자와 앞뒤 공백에 관계없이 KAKAO 로 변환한다.")
    void convertsKakao(String source) {
        assertThat(converter.convert(source)).isEqualTo(OAuth2Vendor.KAKAO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"google", "GOOGLE", "Google"})
    @DisplayName("대소문자에 관계없이 GOOGLE 로 변환한다.")
    void convertsGoogle(String source) {
        assertThat(converter.convert(source)).isEqualTo(OAuth2Vendor.GOOGLE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"naver", "", "code", "kakao2"})
    @DisplayName("지원하지 않는 값이면 변환에 실패한다.")
    void failsForUnsupportedValue(String source) {
        assertThatThrownBy(() -> converter.convert(source)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("enum 에 선언된 모든 벤더를 변환할 수 있다.")
    void convertsEveryDeclaredVendor() {
        for (OAuth2Vendor vendor : OAuth2Vendor.values()) {
            assertThat(converter.convert(vendor.name().toLowerCase())).isEqualTo(vendor);
        }
    }
}
