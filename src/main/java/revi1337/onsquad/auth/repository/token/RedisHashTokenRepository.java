package revi1337.onsquad.auth.repository.token;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;

@Deprecated
@RequiredArgsConstructor
@Component
public class RedisHashTokenRepository implements TokenRepository {

    private static final String TOKEN_KEY = "value";
    private static final String OWNER_KEY = "memberId";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(RefreshToken refreshToken, Long memberId, Duration expired) {
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

    @Override
    public Optional<RefreshToken> findBy(Long memberId) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(cacheName, TOKEN_KEY))
                .map(String::valueOf)
                .map(RefreshToken::new);
    }

    @Override
    public void deleteBy(Long memberId) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        stringRedisTemplate.delete(cacheName);
    }

    @Override
    public void deleteAll() {
        String pattern = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, Sign.ASTERISK);
        Cursor<byte[]> cursor = stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions()
                        .match(pattern)
                        .build());

        List<String> keys = new ArrayList<>();
        while (cursor.hasNext()) {
            keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
        }
        if (!keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
}