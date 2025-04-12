package revi1337.onsquad.inrastructure.mail.repository;

import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.common.application.RedisSafeExecutor;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;

@RequiredArgsConstructor
@Repository("redisCodeRepository")
public class VerificationCodeRedisRepository implements VerificationCodeRepository {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveVerificationCode(String email, String verificationCode, Duration minutes) {
        String redisKey = getKey(email);
        RedisSafeExecutor.run(() ->
                stringRedisTemplate.opsForValue().set(redisKey, verificationCode, minutes)
        );
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
}
