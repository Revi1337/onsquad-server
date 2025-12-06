package revi1337.onsquad.token.infrastructure.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.infrastructure.redis.RedisCacheCleaner;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.domain.repository.TokenRepository;

@RequiredArgsConstructor
@Component
public class RedisHashTokenRepository implements TokenRepository {

    private static final String PREFIX = "user:";
    private static final String TOKEN_KEY = "value";
    private static final String OWNER_KEY = "memberId";

    private final StringRedisTemplate fastStringRedisTemplate;

    @Override
    public void save(RefreshToken refreshToken, Long memberId, Duration expired) {
        String cacheName = getKey(PREFIX + memberId);
        fastStringRedisTemplate.execute((RedisCallback<Object>) (operations) -> {
            operations.multi();
            fastStringRedisTemplate.opsForHash().put(cacheName, OWNER_KEY, String.valueOf(memberId));
            fastStringRedisTemplate.opsForHash().put(cacheName, TOKEN_KEY, refreshToken.value());
            fastStringRedisTemplate.expire(cacheName, expired);
            operations.exec();

            return null;
        });
    }

    @Override
    public Optional<RefreshToken> findBy(Long memberId) {
        String cacheName = getKey(PREFIX + memberId);
        HashOperations<String, Object, Object> hashOperations = fastStringRedisTemplate.opsForHash();

        return Optional.ofNullable(hashOperations.get(cacheName, TOKEN_KEY))
                .map(String::valueOf)
                .map(RefreshToken::new);
    }

    @Override
    public void deleteBy(Long memberId) {
        String cacheName = getKey(PREFIX + memberId);
        fastStringRedisTemplate.delete(cacheName);
    }

    @Override
    public void deleteAll() {
        RedisCacheCleaner.cleanup(fastStringRedisTemplate, getKey(Sign.ASTERISK));
    }

    private String getKey(String identifier) {
        return String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, identifier);
    }
}
