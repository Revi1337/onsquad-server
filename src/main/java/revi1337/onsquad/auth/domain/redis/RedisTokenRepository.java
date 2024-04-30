package revi1337.onsquad.auth.domain.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.auth.dto.response.RefreshToken;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisTokenRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void storeTemporaryRefreshToken(RefreshToken refreshToken, Long id, Duration expired) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(refreshToken.value(), String.valueOf(id), expired);
    }

    public String retrieveTemporaryRefreshToken(RefreshToken refreshToken) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(refreshToken.value());
    }
}