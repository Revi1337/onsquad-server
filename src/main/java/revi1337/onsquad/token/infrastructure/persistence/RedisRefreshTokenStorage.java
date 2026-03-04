package revi1337.onsquad.token.infrastructure.persistence;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.token.application.RefreshTokenStorage;
import revi1337.onsquad.token.domain.model.RefreshToken;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenStorage implements RefreshTokenStorage {

    private static final String KEY_FORMAT = "refresh-token:user:%s";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public long saveToken(Long memberId, RefreshToken refreshToken, Duration expireDuration) {
        String cacheName = getKey(String.valueOf(memberId));
        stringRedisTemplate.opsForValue().set(cacheName, refreshToken.value(), expireDuration);

        return refreshToken.expiredAt().getTime();
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        String cacheName = getKey(String.valueOf(memberId));

        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(cacheName))
                .map(RefreshToken::new);
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        String cacheName = getKey(String.valueOf(memberId));
        RedisCacheEvictor.unlinkKey(stringRedisTemplate, cacheName);
    }

    @Override
    public void deleteAll() {
        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, getKey(Sign.ASTERISK));
    }

    private String getKey(String identifier) {
        String name = String.format(KEY_FORMAT, identifier);

        return String.format(CacheFormat.SIMPLE, name);
    }
}
