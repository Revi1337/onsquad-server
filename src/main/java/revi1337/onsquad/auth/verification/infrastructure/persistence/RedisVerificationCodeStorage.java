package revi1337.onsquad.auth.verification.infrastructure.persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
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

    private static final String ATOMIC_SAVE_VERIFICATION_LUA = """
            redis.call('HMSET', KEYS[1], 'email', ARGV[1], 'code', ARGV[2], 'status', ARGV[3], 'expiredAt', ARGV[4]);
            redis.call('PEXPIRE', KEYS[1], ARGV[5]);
            return 1;
            """;

    private static final String ATOMIC_MARK_VERIFICATION_LUA = """
            if redis.call('EXISTS', KEYS[1]) == 1 then
                redis.call('HSET', KEYS[1], 'status', ARGV[1]);
                redis.call('PEXPIRE', KEYS[1], ARGV[2]);
                return 1;
            else
                return 0;
            end
            """;

    private static final String ATOMIC_VALIDATE_AND_MARK_LUA = """
            local currentCode = redis.call('HGET', KEYS[1], 'code')
            local currentStatus = redis.call('HGET', KEYS[1], 'status')
            if (currentCode == ARGV[1] and currentStatus ~= ARGV[2]) then
                redis.call('HSET', KEYS[1], 'status', ARGV[2])
                redis.call('PEXPIRE', KEYS[1], ARGV[3])
                return 1
            else
                return 0
            end
            """;

    private static final RedisScript<Boolean> ATOMIC_SAVE_VERIFICATION_SCRIPT = RedisScript.of(ATOMIC_SAVE_VERIFICATION_LUA, Boolean.class);
    private static final RedisScript<Boolean> ATOMIC_MARK_VERIFICATION_SCRIPT = RedisScript.of(ATOMIC_MARK_VERIFICATION_LUA, Boolean.class);
    private static final RedisScript<Boolean> ATOMIC_VALIDATE_AND_MARK_SCRIPT = RedisScript.of(ATOMIC_VALIDATE_AND_MARK_LUA, Boolean.class);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public long saveVerificationCode(String email, String code, VerificationStatus status, Duration expireDuration) {
        String redisKey = getKey(email);
        long expiredTime = getExpectExpiredTime(expireDuration);

        RedisSafeExecutor.run(() -> stringRedisTemplate.execute(
                ATOMIC_SAVE_VERIFICATION_SCRIPT,
                Collections.singletonList(redisKey),
                email, code, status.name(), String.valueOf(expiredTime), String.valueOf(expireDuration.toMillis())
        ));

        return expiredTime;
    }

    @Override
    public boolean isValidVerificationCode(String email, String code) {
        String redisKey = getKey(email);
        Object extractedCode = RedisSafeExecutor.supply(
                () -> stringRedisTemplate.opsForHash().get(redisKey, "code")
        );

        return Objects.equals(extractedCode, code);
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus status, Duration expireDuration) {
        String redisKey = getKey(email);

        return RedisSafeExecutor.supply(() -> stringRedisTemplate.execute(
                ATOMIC_MARK_VERIFICATION_SCRIPT,
                Collections.singletonList(redisKey),
                status.name(),
                String.valueOf(expireDuration.toMillis())
        ));
    }

    @Override
    public boolean markVerificationStatusAsSuccess(String email, String authCode, Duration expireDuration) {
        String redisKey = getKey(email);
        return stringRedisTemplate.execute(
                ATOMIC_VALIDATE_AND_MARK_SCRIPT,
                Collections.singletonList(redisKey),
                authCode,
                VerificationStatus.SUCCESS.name(),
                String.valueOf(expireDuration.toMillis())
        );
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
