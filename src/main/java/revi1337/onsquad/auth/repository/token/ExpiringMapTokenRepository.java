package revi1337.onsquad.auth.repository.token;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.auth.application.token.model.RefreshTokenState;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Repository
public class ExpiringMapTokenRepository implements TokenRepository {

    private static final String PREFIX = "user:";
    private static final int MAX_CACHE_SIZE = 10_000;
    private static final Map<String, RefreshTokenState> REFRESH_TRACKER = new ConcurrentHashMap<>();
    private static final ExpiringMap<String, RefreshToken> REFRESH_CACHE = ExpiringMap.builder()
            .maxSize(MAX_CACHE_SIZE)
            .expirationListener((key, value) -> REFRESH_TRACKER.remove(key))
            .variableExpiration()
            .build();

    @Override
    public void save(RefreshToken refreshToken, Long memberId, Duration expired) {
        String inMemoryKey = getKey(PREFIX + memberId);
        long expectedTime = getExpiredTime(expired);

        REFRESH_CACHE.put(inMemoryKey, refreshToken, ExpirationPolicy.CREATED, expired.toMillis(), TimeUnit.MILLISECONDS);
        REFRESH_TRACKER.put(inMemoryKey, new RefreshTokenState(refreshToken, String.valueOf(memberId), expectedTime));
    }

    @Override
    public Optional<RefreshToken> findBy(Long memberId) {
        String inMemoryKey = getKey(PREFIX + memberId);
        return Optional.ofNullable(REFRESH_CACHE.get(inMemoryKey));
    }

    @Override
    public void deleteBy(Long memberId) {
        String inMemoryKey = getKey(PREFIX + memberId);
        REFRESH_TRACKER.remove(inMemoryKey);
        REFRESH_CACHE.remove(inMemoryKey);
    }

    @Override
    public void deleteAll() {
        REFRESH_TRACKER.clear();
        REFRESH_CACHE.clear();
    }

    public Map<String, RefreshTokenState> findAllState() {
        return Collections.unmodifiableMap(REFRESH_TRACKER);
    }

    private long getExpiredTime(Duration duration) {
        return Instant.now()
                .plusMillis(duration.toMillis())
                .toEpochMilli();
    }

    private String getKey(String identifier) {
        return String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, identifier);
    }
}
