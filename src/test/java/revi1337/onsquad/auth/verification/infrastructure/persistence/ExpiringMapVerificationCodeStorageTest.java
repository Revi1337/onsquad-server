package revi1337.onsquad.auth.verification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.LocalDateTime;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationCodes;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

class ExpiringMapVerificationCodeStorageTest {

    private final ExpiringMapVerificationCodeStorage storage = new ExpiringMapVerificationCodeStorage();
    private final ExpiringMap<String, VerificationCode> verificationStore =
            (ExpiringMap<String, VerificationCode>) ReflectionTestUtils.getField(storage, "verificationStore");

    private final String email = "user@test.com";
    private final String key = "onsquad:verification-code:user@test.com";

    @BeforeEach
    void setUp() {
        verificationStore.clear();
    }

    @Nested
    @DisplayName("인증 코드 저장")
    class SaveVerificationCode {

        @Test
        @DisplayName("인증 코드를 저장하면 이메일, 코드, 상태, 만료시간이 포함된 객체가 Store에 기록된다")
        void recordVerificationCodeObject() {
            String code = "123456";
            Duration duration = Duration.ofMinutes(5);
            VerificationStatus status = VerificationStatus.PENDING;

            storage.saveVerificationCode(email, code, status, duration);

            VerificationCode result = verificationStore.get(key);
            assertSoftly(softly -> {
                softly.assertThat(result).isNotNull();
                softly.assertThat(result.getEmail()).isEqualTo(email);
                softly.assertThat(result.getCode()).isEqualTo(code);
                softly.assertThat(result.getStatus()).isSameAs(status);
                softly.assertThat(result.getExpiredAt()).isAfter(LocalDateTime.now());
            });
        }
    }

    @Nested
    @DisplayName("인증 코드 일치 여부 확인")
    class IsValidVerificationCode {

        @Test
        @DisplayName("입력한 코드가 저장된 인증 코드와 일치하면 참을 반환한다")
        void returnTrueWhenCodeMatches() {
            storage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));

            assertThat(storage.isValidVerificationCode(email, "123456")).isTrue();
        }

        @Test
        @DisplayName("입력한 코드가 다르거나 데이터가 없으면 거짓을 반환한다")
        void returnFalseWhenCodeMismatchedOrEmpty() {
            storage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));

            assertSoftly(softly -> {
                softly.assertThat(storage.isValidVerificationCode(email, "wrong")).isFalse();
                softly.assertThat(storage.isValidVerificationCode("other@test.com", "123456")).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("인증 상태 마킹")
    class MarkVerificationStatus {

        @Test
        @DisplayName("기존 인증 데이터가 존재하면 코드는 유지하되 상태와 만료 시간을 갱신한다")
        void updateStatusAndExpirationWhenExists() {
            storage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));

            boolean marked = storage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(10));

            assertSoftly(softly -> {
                VerificationCode result = verificationStore.get(key);
                softly.assertThat(marked).isTrue();
                softly.assertThat(result.getCode()).isEqualTo("123456");
                softly.assertThat(result.getStatus()).isSameAs(VerificationStatus.SUCCESS);
            });
        }

        @Test
        @DisplayName("데이터가 이미 만료되었거나 존재하지 않으면 상태 변경 요청을 무시한다")
        void ignoreRequestWhenDataNotFound() {
            boolean marked = storage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(10));

            assertThat(marked).isFalse();
        }
    }

    @Nested
    @DisplayName("특정 상태 마킹 여부 조회")
    class IsMarkedVerificationStatusWith {

        @Test
        @DisplayName("현재 저장된 데이터의 상태가 요청한 상태와 일치하는지 검증한다")
        void verifyCurrentStatusMatches() {
            storage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));
            storage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(5));

            assertThat(storage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS)).isTrue();
            assertThat(storage.isMarkedVerificationStatusWith(email, VerificationStatus.PENDING)).isFalse();
        }
    }

    @Nested
    @DisplayName("전체 인증 데이터 수집")
    class GetVerificationCodes {

        @Test
        @DisplayName("현재 메모리에 관리 중인 모든 인증 도메인 객체를 리스트로 반환한다")
        void collectAllDomainObjects() {
            storage.saveVerificationCode("user1@test.com", "c1", VerificationStatus.PENDING, Duration.ofMinutes(5));
            storage.saveVerificationCode("user2@test.com", "c2", VerificationStatus.PENDING, Duration.ofMinutes(5));

            VerificationCodes result = storage.getVerificationCodes();

            assertThat(result.verificationCodes()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("데이터 자동 만료")
    class DataExpiration {

        @Test
        @DisplayName("설정된 유효 시간이 지나면 메모리 저장소에서 데이터가 자동으로 제거된다")
        void autoRemoveAfterExpiration() throws InterruptedException {
            storage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMillis(100));

            Thread.sleep(200);

            assertThat(verificationStore.containsKey(key)).isFalse();
        }
    }
}
