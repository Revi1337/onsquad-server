package revi1337.onsquad.auth.verification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.container.UnreachableRedis;

class RedisVerificationCodeStorageFailureTest {

    private final RedisVerificationCodeStorage storage = new RedisVerificationCodeStorage(UnreachableRedis.stringRedisTemplate());
    private final String email = "user@test.com";

    @Test
    @DisplayName("저장은 Redis 연결 실패를 변환 없이 DataAccessException 으로 전파한다")
    void saveVerificationCode() {
        assertThatThrownBy(() -> storage.saveVerificationCode(email, "123456", VerificationStatus.PENDING, Duration.ofMinutes(5)))
                .isInstanceOf(DataAccessException.class)
                .isInstanceOf(RedisConnectionFailureException.class)
                .isNotInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("코드 검증은 Redis 연결 실패를 변환 없이 DataAccessException 으로 전파한다")
    void isValidVerificationCode() {
        assertThatThrownBy(() -> storage.isValidVerificationCode(email, "123456"))
                .isInstanceOf(DataAccessException.class)
                .isNotInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("상태 마킹은 Redis 연결 실패를 변환 없이 DataAccessException 으로 전파한다")
    void markVerificationStatus() {
        assertThatThrownBy(() -> storage.markVerificationStatus(email, VerificationStatus.SUCCESS, Duration.ofMinutes(5)))
                .isInstanceOf(DataAccessException.class)
                .isNotInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("인증 성공 마킹은 Redis 연결 실패를 변환 없이 DataAccessException 으로 전파한다")
    void markVerificationStatusAsSuccess() {
        assertThatThrownBy(() -> storage.markVerificationStatusAsSuccess(email, "123456", Duration.ofMinutes(5)))
                .isInstanceOf(DataAccessException.class)
                .isNotInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("상태 확인은 Redis 연결 실패를 변환 없이 DataAccessException 으로 전파한다")
    void isMarkedVerificationStatusWith() {
        assertThatThrownBy(() -> storage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS))
                .isInstanceOf(DataAccessException.class)
                .isNotInstanceOf(IllegalStateException.class);
    }
}
