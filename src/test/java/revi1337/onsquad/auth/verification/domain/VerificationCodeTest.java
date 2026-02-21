package revi1337.onsquad.auth.verification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerificationCodeTest {

    @Test
    @DisplayName("EpochMilli 기반으로 만료 시각(LocalDateTime)이 정확히 계산된다")
    void constructor() {
        String email = "test@email.com";
        String code = "12345";
        VerificationStatus status = VerificationStatus.PENDING;
        long epochMilli = LocalDate.of(2026, 1, 4).atStartOfDay()
                .plusMinutes(2)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        VerificationCode verificationCode = new VerificationCode(email, code, status, epochMilli);

        assertSoftly(softly -> {
            softly.assertThat(verificationCode.getEmail()).isEqualTo(email);
            softly.assertThat(verificationCode.getCode()).isEqualTo(code);
            softly.assertThat(verificationCode.getStatus()).isSameAs(status);
            softly.assertThat(verificationCode.getExpiredAt()).isEqualTo(LocalDateTime.of(2026, 1, 4, 0, 2));
        });
    }

    @Test
    @DisplayName("만료 시각이 지난 시점에는 사용할 수 없다")
    void isAvailableAt() {
        String email = "test@email.com";
        String code = "12345";
        VerificationStatus status = VerificationStatus.PENDING;
        long epochMilli = LocalDate.of(2026, 1, 4).atStartOfDay()
                .plusMinutes(2)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        VerificationCode verificationCode = new VerificationCode(email, code, status, epochMilli);

        boolean available = verificationCode.isAvailableAt(LocalDate.of(2026, 1, 5).atStartOfDay());

        assertThat(available).isFalse();
    }

    @Test
    @DisplayName("만료 시각 이전 시점에는 사용할 수 있다")
    void isAvailableAt2() {
        String email = "test@email.com";
        String code = "12345";
        VerificationStatus status = VerificationStatus.PENDING;
        long epochMilli = LocalDate.of(2026, 1, 4).atStartOfDay()
                .plusMinutes(2)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        VerificationCode verificationCode = new VerificationCode(email, code, status, epochMilli);

        boolean available = verificationCode.isAvailableAt(LocalDate.of(2026, 1, 2).atStartOfDay());

        assertThat(available).isTrue();
    }
}
