package revi1337.onsquad.auth.verification.infrastructure;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.auth.verification.domain.VerificationCodeRepository;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.infrastructure.storage.redis.RedisSafeExecutor;

@RequiredArgsConstructor
@Order(1)
@Repository
public class VerificationCodeRedisRepository implements VerificationCodeRepository {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public long saveVerificationCode(String email, String verificationCode, Duration minutes) {
        String redisKey = getKey(email);
        long expiredTime = getExpiredTime(minutes);
        RedisSafeExecutor.run(() -> stringRedisTemplate.opsForValue().set(redisKey, verificationCode, minutes.toMillis(), TimeUnit.MILLISECONDS));

        return expiredTime;
    }

    @Override
    public boolean isValidVerificationCode(String email, String verificationCode) {
        String redisKey = getKey(email);
        String extractedCode = RedisSafeExecutor.supply(() -> stringRedisTemplate.opsForValue().get(redisKey));

        return Objects.equals(extractedCode, verificationCode);
    }

    @Override
    public boolean markVerificationStatus(String email, VerificationStatus verificationStatus, Duration minutes) {
        String redisKey = getKey(email);

        return RedisSafeExecutor.supply(() ->
                stringRedisTemplate.opsForValue().setIfPresent(redisKey, verificationStatus.name(), minutes)
        );
    }

    @Override
    public boolean isMarkedVerificationStatusWith(String email, VerificationStatus verificationStatus) {
        String redisKey = getKey(email);

        return RedisSafeExecutor.supply(() ->
                Objects.equals(stringRedisTemplate.opsForValue().get(redisKey), verificationStatus.name())
        );
    }

    private String getKey(String email) {
        return String.format(CacheFormat.COMPLEX, CacheConst.VERIFICATION_CODE, email);
    }

    private long getExpiredTime(Duration duration) {
        return Instant.now()
                .plusMillis(duration.toMillis())
                .atZone(TimeZone.getDefault().toZoneId())
                .toInstant()
                .toEpochMilli();
    }
}
