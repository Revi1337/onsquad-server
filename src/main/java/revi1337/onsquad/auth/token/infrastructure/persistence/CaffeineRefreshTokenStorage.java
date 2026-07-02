package revi1337.onsquad.auth.token.infrastructure.persistence;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.token.application.RefreshTokenStorage;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.auth.token.domain.model.RefreshTokens;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.infrastructure.storage.caffeine.TimedEntry;

@Component
public class CaffeineRefreshTokenStorage implements RefreshTokenStorage {

    private static final String KEY_FORMAT = "refresh-token:user:%s";
    private static final int MAX_CACHE_SIZE = 10_000;

    private static final Expiry<String, TimedEntry<RefreshToken>> EXPIRY = new Expiry<>() {
        @Override
        public long expireAfterCreate(String key, TimedEntry<RefreshToken> entry, long currentTime) {
            return entry.ttlNanos();
        }

        @Override
        public long expireAfterUpdate(String key, TimedEntry<RefreshToken> entry, long currentTime, long currentDuration) {
            return entry.ttlNanos();
        }

        @Override
        public long expireAfterRead(String key, TimedEntry<RefreshToken> entry, long currentTime, long currentDuration) {
            return currentDuration;
        }
    };

    private final Cache<String, TimedEntry<RefreshToken>> refreshStore =
            Caffeine.newBuilder()
                    .maximumSize(MAX_CACHE_SIZE)
                    .expireAfter(EXPIRY)
                    .build();

    @Override
    public long saveToken(Long memberId, RefreshToken refreshToken, Duration expireDuration) {
        String inMemoryKey = getKey(String.valueOf(memberId));
        long expectedTime = getExpiredTime(expireDuration);
        refreshStore.put(inMemoryKey, new TimedEntry<>(refreshToken, expireDuration.toNanos()));

        return expectedTime;
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        String inMemoryKey = getKey(String.valueOf(memberId));

        return Optional.ofNullable(refreshStore.getIfPresent(inMemoryKey)).map(TimedEntry::value);
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        String inMemoryKey = getKey(String.valueOf(memberId));
        refreshStore.invalidate(inMemoryKey);
    }

    @Override
    public void deleteAll() {
        refreshStore.invalidateAll();
    }

    public RefreshTokens getTokens() {
        List<RefreshToken> tokens = refreshStore.asMap().values().stream()
                .map(TimedEntry::value)
                .toList();

        return new RefreshTokens(tokens);
    }

    private long getExpiredTime(Duration duration) {
        return Instant.now()
                .plusMillis(duration.toMillis())
                .toEpochMilli();
    }

    private String getKey(String identifier) {
        String name = String.format(KEY_FORMAT, identifier);

        return String.format(CacheFormat.SIMPLE, name);
    }
}
