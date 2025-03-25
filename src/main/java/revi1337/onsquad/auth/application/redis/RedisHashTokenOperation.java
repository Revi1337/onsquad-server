package revi1337.onsquad.auth.application.redis;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.token.RefreshToken;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Deprecated
@RequiredArgsConstructor
@Component
public class RedisHashTokenOperation {

    private static final String TOKEN_KEY = "value";
    private static final String OWNER_KEY = "memberId";

    private final StringRedisTemplate stringRedisTemplate;

    public void storeTemporaryRefreshToken(RefreshToken refreshToken, Long memberId, Duration expired) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        stringRedisTemplate.execute((RedisCallback<Object>) (operations) -> {
            operations.multi();
            stringRedisTemplate.opsForHash().put(cacheName, OWNER_KEY, String.valueOf(memberId));
            stringRedisTemplate.opsForHash().put(cacheName, TOKEN_KEY, refreshToken.value());
            stringRedisTemplate.expire(cacheName, expired);
            operations.exec();
            return null;
        });
    }

    public Optional<RefreshToken> retrieveTemporaryRefreshToken(Long memberId) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(cacheName, TOKEN_KEY))
                .map(String::valueOf)
                .map(RefreshToken::of);
    }

    public void deleteTemporaryRefreshToken(Long memberId) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        stringRedisTemplate.delete(cacheName);
    }
}