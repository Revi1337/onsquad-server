package revi1337.onsquad.auth.verification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;

@Import(RedisVerificationCodeStorage.class)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
@DataRedisTest
class RedisVerificationCodeStorageTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisVerificationCodeStorage redisVerificationCodeStorage;

    private final String email = "user@test.com";
    private final String key = "onsquad:verification-code:user@test.com";

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Nested
    @DisplayName("인증 코드 저장")
    class SaveVerificationCode {

        @Test
        @DisplayName("인증 코드를 저장하면 Redis Hash 구조로 모든 필드가 기록되며 설정된 TTL이 적용된다")
        void storeAsRedisHashWithTTL() {
            String code = "123456";
            Duration duration = Duration.ofMinutes(5);
            VerificationStatus status = VerificationStatus.PENDING;

            redisVerificationCodeStorage.saveVerificationCode(email, code, status, duration);

            assertSoftly(softly -> {
                softly.assertThat(stringRedisTemplate.opsForHash().get(key, "code")).isEqualTo(code);
                softly.assertThat(stringRedisTemplate.opsForHash().get(key, "status")).isEqualTo(status.name());
                softly.assertThat(stringRedisTemplate.opsForHash().get(key, "email")).isEqualTo(email);
                softly.assertThat(stringRedisTemplate.getExpire(key)).isPositive();
            });
        }
    }

    @Nested
    @DisplayName("인증 코드 유효성 검증")
    class IsValidVerificationCode {

        @Test
        @DisplayName("Redis Hash에 저장된 'code' 필드 값이 입력값과 일치하면 참을 반환한다")
        void returnTrueWhenHashFieldMatches() {
            redisVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));

            assertThat(redisVerificationCodeStorage.isValidVerificationCode(email, "123456")).isTrue();
        }

        @Test
        @DisplayName("데이터가 존재하지 않거나 저장된 코드와 다르면 거짓을 반환한다")
        void returnFalseWhenDataMissingOrMismatched() {
            redisVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));

            assertThat(redisVerificationCodeStorage.isValidVerificationCode(email, "wrong")).isFalse();
            assertThat(redisVerificationCodeStorage.isValidVerificationCode("other@test.com", "123456")).isFalse();
        }
    }

    @Nested
    @DisplayName("인증 상태 마킹")
    class MarkVerificationStatus {

        @Test
        @DisplayName("기존 Hash 데이터가 존재하는 경우 status 필드만 성공 상태로 갱신하고 유효 시간을 재설정한다")
        void updateStatusFieldAndResetTTL() {
            redisVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));

            boolean marked = redisVerificationCodeStorage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(10));

            assertSoftly(softly -> {
                softly.assertThat(marked).isTrue();
                softly.assertThat(stringRedisTemplate.opsForHash().get(key, "status")).isEqualTo(VerificationStatus.SUCCESS.name());
                softly.assertThat(stringRedisTemplate.getExpire(key)).isGreaterThan(590);
            });
        }

        @Test
        @DisplayName("데이터가 이미 만료되어 존재하지 않으면 상태 변경 요청을 무시하고 거짓을 반환한다")
        void failWhenKeyAlreadyExpired() {
            boolean marked = redisVerificationCodeStorage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(10));

            assertThat(marked).isFalse();
        }
    }

    @Nested
    @DisplayName("특정 상태 마킹 여부 조회")
    class IsMarkedVerificationStatusWith {

        @Test
        @DisplayName("Redis Hash의 'status' 필드 값이 요청한 상태와 일치하는지 검증한다")
        void verifyStatusFieldMatchesInRedis() {
            redisVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5));
            redisVerificationCodeStorage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(5));

            assertThat(redisVerificationCodeStorage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS)).isTrue();
            assertThat(redisVerificationCodeStorage.isMarkedVerificationStatusWith(email, VerificationStatus.PENDING)).isFalse();
        }
    }

    @Nested
    @DisplayName("데이터 자동 만료")
    class DataExpiration {

        @Test
        @DisplayName("설정된 유효 시간(TTL)이 경과하면 Redis 엔진에 의해 해당 Hash 키가 완전히 삭제된다")
        void autoPurgeByKeyExpiration() throws InterruptedException {
            redisVerificationCodeStorage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMillis(100));

            Thread.sleep(200);

            assertThat(stringRedisTemplate.hasKey(key)).isFalse();
        }
    }
}
