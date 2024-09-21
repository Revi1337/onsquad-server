package revi1337.onsquad.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.token.RefreshToken;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RedisHashTokenOperation {

    private static final String HASH_NAME_FORMAT = "onsquad:refresh:user:id:%d";
    private static final String TOKEN_OWNER_KEY = "memberId";
    private static final String TOKEN_KEY = "value";

    private final StringRedisTemplate stringRedisTemplate;

    public void storeTemporaryRefreshToken(RefreshToken refreshToken, Long memberId, Duration expired) {
        String hashName = String.format(HASH_NAME_FORMAT, memberId);
        stringRedisTemplate.execute((RedisCallback<Object>) (operations) -> {
            operations.multi();
            stringRedisTemplate.opsForHash().put(hashName, TOKEN_OWNER_KEY, String.valueOf(memberId));
            stringRedisTemplate.opsForHash().put(hashName, TOKEN_KEY, refreshToken.value());
            stringRedisTemplate.expire(hashName, expired);
            operations.exec();
            return null;
        });
    }

    public Optional<RefreshToken> retrieveTemporaryRefreshToken(Long memberId) {
        String hashName = String.format(HASH_NAME_FORMAT, memberId);
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(hashName, TOKEN_KEY))
                .map(String::valueOf)
                .map(RefreshToken::of);
    }

    public void deleteTemporaryRefreshToken(Long memberId) {
        String hashName = String.format(HASH_NAME_FORMAT, memberId);
        stringRedisTemplate.delete(hashName);
    }

    public void updateTemporaryRefreshToken(Long memberId, RefreshToken refreshToken) {
        String hashName = String.format(HASH_NAME_FORMAT, memberId);
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        hashOperations.put(hashName, TOKEN_KEY, refreshToken.value());
    }
}