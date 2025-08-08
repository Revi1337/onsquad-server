package revi1337.onsquad.inrastructure.mail.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerificationSnapshotTest {

    @Test
    @DisplayName("사용할 수 있는 VerificationSnapshot인지 검증에 성공한다.")
    void success1() {
        Instant now = Instant.now();
        long plusTime = now.plusSeconds(60).toEpochMilli();
        String key = "key";
        VerificationState verificationState = new VerificationState("200", "email", plusTime);
        VerificationSnapshot verificationSnapshot = new VerificationSnapshot(key, verificationState);

        long nowTime = now.toEpochMilli();
        assertThat(verificationSnapshot.canUse(nowTime)).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 VerificationState 검증에 성공한다.")
    void success2() {
        Instant now = Instant.now();
        long nowTime = now.toEpochMilli();
        String key = "key";
        VerificationState verificationState = new VerificationState("200", "email", nowTime);
        VerificationSnapshot verificationSnapshot = new VerificationSnapshot(key, verificationState);

        long plusTime = now.plusSeconds(60).toEpochMilli();
        assertThat(verificationSnapshot.canUse(plusTime)).isFalse();
    }
}