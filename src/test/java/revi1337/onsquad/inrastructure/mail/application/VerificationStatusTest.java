package revi1337.onsquad.inrastructure.mail.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerificationStatusTest {

    @Test
    @DisplayName("VerificationStatus 의 Text 로 VerificationStatus 를 지원하는지 검증한다.")
    void success() {
        String code = "500";

        boolean supports = VerificationStatus.supports(code);

        assertThat(supports).isFalse();
    }
}