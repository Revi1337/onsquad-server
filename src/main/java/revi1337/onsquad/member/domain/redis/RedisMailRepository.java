package revi1337.onsquad.member.domain.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.inrastructure.mail.MailStatus;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class RedisMailRepository {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String KEY_FORMAT = "onsquad:auth:mail:%s";

    public void saveAuthCode(String email, String authCode, Duration minutes) {
        String emailFormat = String.format(KEY_FORMAT, email);
        stringRedisTemplate.opsForValue().set(emailFormat, authCode, minutes);
    }

    public boolean isValidMailAuthCode(String email, String confirm) {
        String redisAuthKey = String.format(KEY_FORMAT, email);
        return Objects.equals(stringRedisTemplate.opsForValue().get(redisAuthKey), confirm);
    }

    public boolean overwriteAuthCodeToStatus(String email, MailStatus mailStatus, Duration minutes) {
        String redisAuthKey = String.format(KEY_FORMAT, email);
        return Boolean.TRUE.equals(stringRedisTemplate
                .opsForValue()
                .setIfPresent(redisAuthKey, mailStatus.getText(), minutes));
    }

    public boolean isValidMailStatus(String email, MailStatus mailStatus) {
        String redisAuthKey = String.format(KEY_FORMAT, email);
        return Objects.equals(
                stringRedisTemplate.opsForValue().get(redisAuthKey), mailStatus.getText()
        );
    }
}
