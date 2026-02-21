package revi1337.onsquad.auth.verification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerificationCodesTest {

    @Test
    @DisplayName("특정 시각을 기준으로 아직 만료되지 않은(isAfter) 인증 코드들만 추출한다")
    void extractAvailableBefore() {
        VerificationCode verificationCode1 = new VerificationCode("test@email.com", "12345", VerificationStatus.PENDING, getExpireTime(2026, 1, 2));
        VerificationCode verificationCode2 = new VerificationCode("test@email.com", "12345", VerificationStatus.PENDING, getExpireTime(2026, 1, 3));
        VerificationCode verificationCode3 = new VerificationCode("test@email.com", "12345", VerificationStatus.PENDING, getExpireTime(2026, 1, 4));
        VerificationCode verificationCode4 = new VerificationCode("test@email.com", "12345", VerificationStatus.PENDING, getExpireTime(2026, 1, 5));
        VerificationCode verificationCode5 = new VerificationCode("test@email.com", "12345", VerificationStatus.PENDING, getExpireTime(2026, 1, 6));
        VerificationCodes codes = new VerificationCodes(List.of(verificationCode1, verificationCode2, verificationCode3, verificationCode4, verificationCode5));

        List<VerificationCode> available = codes.extractAvailableBefore(LocalDate.of(2026, 1, 4).atStartOfDay());

        assertThat(available).hasSize(3);
    }

    private long getExpireTime(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth).atStartOfDay()
                .plusMinutes(2)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
