package revi1337.onsquad.auth.verification.infrastructure.persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.infrastructure.storage.redis.RedisSafeExecutor;

@Order(1)
@Component
@RequiredArgsConstructor
public class RedisVerificationCodeStorage implements VerificationCodeStorage {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public long saveVerificationCode(String email, String code, VerificationStatus status, Duration minutes) {
        String redisKey = getKey(email);
        long expiredTime = getExpectExpiredTime(minutes);

        Map<String, String> map = Map.of(
                "email", email,
                "code", code,
                "status", status.name(),
                "expiredAt", String.valueOf(expiredTime)
        );

        RedisSafeExecutor.run(() -> {
            stringRedisTemplate.opsForHash().putAll(redisKey, map);
            stringRedisTemplate.expire(redisKey, minutes);
        });

        return expiredTime;
    }

    @Override
    public boolean isValidVerificationCode(String email, String code) {
        String redisKey = getKey(email);
        String extractedCode = RedisSafeExecutor.supply(
                () -> String.valueOf(stringRedisTemplate.opsForHash().get(redisKey, "code"))
        );

        return Objects.equals(extractedCode, code);
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus status, Duration minutes) {
        String redisKey = getKey(email);

        return RedisSafeExecutor.supply(() -> {
            if (stringRedisTemplate.hasKey(redisKey)) {
                stringRedisTemplate.opsForHash().put(redisKey, "status", status.name());
                stringRedisTemplate.expire(redisKey, minutes);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus status) {
        String redisKey = getKey(email);

        return RedisSafeExecutor.supply(() ->
                Objects.equals(stringRedisTemplate.opsForHash().get(redisKey, "status"), status.name())
        );
    }

    private String getKey(String email) {
        return String.format(CacheFormat.COMPLEX, CacheConst.VERIFICATION_CODE, email);
    }

    private long getExpectExpiredTime(Duration duration) {
        return Instant.now()
                .plus(duration)
                .toEpochMilli();
    }
}
