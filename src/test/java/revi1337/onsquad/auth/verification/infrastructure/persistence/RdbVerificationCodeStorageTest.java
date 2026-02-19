package revi1337.onsquad.auth.verification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;

@Import(RdbVerificationCodeStorage.class)
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@JdbcTest
class RdbVerificationCodeStorageTest {

    @Autowired
    private RdbVerificationCodeStorage rdbVerificationCodeStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String email = "user@test.com";

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS verification_code");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS verification_code(
                    email      VARCHAR(255) NOT NULL,
                    code       VARCHAR(50)  NOT NULL,
                    status     VARCHAR(20)  NOT NULL,
                    expired_at BIGINT       NOT NULL,
                    PRIMARY KEY (email)
                );
                """);
    }

    @Nested
    @DisplayName("인증 코드 저장")
    class SaveVerificationCode {

        @Test
        @DisplayName("인증 코드를 저장하면 DB에 레코드가 기록되며 만료 시간이 Milliseconds로 저장된다")
        void storeInDatabase() {
            String code = "123456";
            Duration duration = Duration.ofMinutes(5);
            VerificationStatus status = VerificationStatus.PENDING;

            rdbVerificationCodeStorage.saveVerificationCode(email, code, status, duration);

            assertSoftly(softly -> {
                Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM verification_code WHERE email = ?", email);
                softly.assertThat(result.get("email")).isEqualTo(email);
                softly.assertThat(result.get("code")).isEqualTo(code);
                softly.assertThat(result.get("status")).isEqualTo(status.name());
                softly.assertThat((Long) result.get("expired_at")).isGreaterThan(Instant.now().toEpochMilli());
            });
        }
    }

    @Nested
    @DisplayName("인증 코드 유효성 검증")
    class IsValidVerificationCode {

        @Test
        @DisplayName("DB에 저장된 코드와 입력값이 일치하고 만료되지 않았으면 참을 반환한다")
        void returnTrueWhenCodeMatchesAndNotExpired() {
            String code = "123456";
            rdbVerificationCodeStorage.saveVerificationCode(email, code, VerificationStatus.PENDING, Duration.ofMinutes(5));

            assertThat(rdbVerificationCodeStorage.isValidVerificationCode(email, code)).isTrue();
        }

        @Test
        @DisplayName("코드가 다르거나 만료 시간이 지난 경우 거짓을 반환한다")
        void returnFalseWhenMismatchedOrExpired() {
            rdbVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMillis(-1000)); // 이미 만료

            assertSoftly(softly -> {
                softly.assertThat(rdbVerificationCodeStorage.isValidVerificationCode(email, "123456")).isFalse();
                softly.assertThat(rdbVerificationCodeStorage.isValidVerificationCode(email, "wrong")).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("인증 상태 마킹")
    class MarkVerificationStatus {

        @Test
        @DisplayName("기존 레코드가 존재하면 상태와 유효 시간을 갱신하고 참을 반환한다")
        void updateStatusAndExpiration() {
            rdbVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));

            boolean marked = rdbVerificationCodeStorage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(10));

            assertSoftly(softly -> {
                Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM verification_code WHERE email = ?", email);
                softly.assertThat(marked).isTrue();
                softly.assertThat(result.get("status")).isEqualTo(VerificationStatus.SUCCESS.name());
            });
        }

        @Test
        @DisplayName("데이터가 존재하지 않으면 거짓을 반환한다")
        void returnFalseWhenRecordNotFound() {
            boolean marked = rdbVerificationCodeStorage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(10));

            assertThat(marked).isFalse();
        }
    }

    @Nested
    @DisplayName("원자적 인증 성공 마킹 (Atomic Update Query)")
    class MarkVerificationStatusAsSuccess {

        @Test
        @DisplayName("이메일, 코드, 만료 여부 조건을 모두 만족하고 아직 성공 상태가 아니면, SUCCESS로 변경하고 참을 반환한다")
        void returnTrueAndUpdateWhenConditionsMet() {
            String authCode = "123456";
            rdbVerificationCodeStorage.saveVerificationCode(email, authCode, VerificationStatus.PENDING, Duration.ofMinutes(5));

            boolean result = rdbVerificationCodeStorage.markVerificationStatusAsSuccess(email, authCode, Duration.ofMinutes(10));

            assertSoftly(softly -> {
                Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM verification_code WHERE email = ?", email);
                softly.assertThat(result).isTrue();
                softly.assertThat(data.get("status")).isEqualTo(VerificationStatus.SUCCESS.name());
            });
        }

        @Test
        @DisplayName("코드가 틀리거나 이미 만료된 경우 업데이트가 수행되지 않고 거짓을 반환한다")
        void returnFalseWhenCodeIsWrongOrExpired() {
            rdbVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMillis(-1000));

            boolean result = rdbVerificationCodeStorage.markVerificationStatusAsSuccess(email, "wrong_code", Duration.ofMinutes(10));

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("이미 SUCCESS 상태인 경우 WHERE 조건(status != SUCCESS)에 의해 업데이트되지 않고 거짓을 반환한다")
        void returnFalseWhenAlreadySuccessStatus() {
            String authCode = "123456";
            rdbVerificationCodeStorage.saveVerificationCode(email, authCode, VerificationStatus.SUCCESS, Duration.ofMinutes(5));

            boolean result = rdbVerificationCodeStorage.markVerificationStatusAsSuccess(email, authCode, Duration.ofMinutes(10));

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("특정 상태 마킹 여부 조회")
    class IsMarkedVerificationStatusWith {

        @Test
        @DisplayName("DB의 status 컬럼 값이 요청한 상태와 일치하는지 검증한다")
        void verifyStatusColumnMatches() {
            rdbVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));
            rdbVerificationCodeStorage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(5));

            assertThat(rdbVerificationCodeStorage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS)).isTrue();
            assertThat(rdbVerificationCodeStorage.isMarkedVerificationStatusWith(email, VerificationStatus.PENDING)).isFalse();
        }
    }

    @Nested
    @DisplayName("만료된 인증 코드 대량 삭제")
    class RemoveExpiredCodes {

        @Test
        @DisplayName("입력된 기준 시간(baseTimeMillis)보다 이전에 만료된 레코드만 삭제하고 삭제된 건수를 반환한다")
        void deleteOnlyExpiredRecordsBasedOnBaseTime() {
            long now = Instant.now().toEpochMilli();
            long baseTime = now;
            rdbVerificationCodeStorage.saveVerificationCode("expired1@test.com", "111", VerificationStatus.PENDING, Duration.ofMillis(-10000));
            rdbVerificationCodeStorage.saveVerificationCode("expired2@test.com", "222", VerificationStatus.PENDING, Duration.ofMillis(-5000));
            rdbVerificationCodeStorage.saveVerificationCode("valid@test.com", "333", VerificationStatus.PENDING, Duration.ofDays(1));

            int deletedCount = rdbVerificationCodeStorage.removeExpiredCodes(baseTime);

            assertSoftly(softly -> {
                softly.assertThat(deletedCount).isEqualTo(2);
                softly.assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM verification_code", Long.class))
                        .isEqualTo(1L);
                softly.assertThat(jdbcTemplate.queryForObject("SELECT email FROM verification_code", String.class))
                        .isEqualTo("valid@test.com");
            });
        }

        @Test
        @DisplayName("만료된 데이터가 하나도 없는 경우 0을 반환하며 아무 데이터도 삭제하지 않는다")
        void returnZeroWhenNoExpiredRecords() {
            long now = Instant.now().toEpochMilli();
            rdbVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(10));

            int deletedCount = rdbVerificationCodeStorage.removeExpiredCodes(now);

            assertSoftly(softly -> {
                softly.assertThat(deletedCount).isEqualTo(0);
                softly.assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM verification_code", Integer.class))
                        .isEqualTo(1);
            });
        }
    }
}
