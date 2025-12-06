package revi1337.onsquad.member.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerificationStateTest {

    @Test
    @DisplayName("유효한 VerificationState 검즘에 성공한다.")
    void success1() {
        Instant now = Instant.now();
        long plusTime = now.plusSeconds(60).toEpochMilli();

        VerificationState verificationState = new VerificationState("200", "email", plusTime);

        long nowTime = now.toEpochMilli();
        assertThat(verificationState.canUse(nowTime)).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 VerificationState 검증에 성공한다.")
    void success2() {
        Instant now = Instant.now();
        long nowTime = now.toEpochMilli();

        VerificationState verificationState = new VerificationState("200", "email", nowTime);

        long plusTime = now.plusSeconds(60).toEpochMilli();
        assertThat(verificationState.canUse(plusTime)).isFalse();
    }
}
