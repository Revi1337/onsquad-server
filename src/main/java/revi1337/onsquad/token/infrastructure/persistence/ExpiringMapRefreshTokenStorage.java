package revi1337.onsquad.token.infrastructure.persistence;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.token.application.RefreshTokenStorage;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.domain.model.RefreshTokens;

@Component
@RequiredArgsConstructor
public class ExpiringMapRefreshTokenStorage implements RefreshTokenStorage {

    private static final String KEY_FORMAT = "refresh-token:user:%s";
    private static final int MAX_CACHE_SIZE = 10_000;

    private final ExpiringMap<String, RefreshToken> refreshStore = ExpiringMap.builder().maxSize(MAX_CACHE_SIZE)
            .maxSize(MAX_CACHE_SIZE)
            .variableExpiration()
            .build();

    @Override
    public long saveToken(Long memberId, RefreshToken refreshToken, Duration expireDuration) {
        String inMemoryKey = getKey(String.valueOf(memberId));
        long expectedTime = getExpiredTime(expireDuration);
        refreshStore.put(inMemoryKey, refreshToken, ExpirationPolicy.CREATED, expireDuration.toMillis(), TimeUnit.MILLISECONDS);

        return expectedTime;
    }

    @Override
    public Optional<RefreshToken> findTokenBy(Long memberId) {
        String inMemoryKey = getKey(String.valueOf(memberId));

        return Optional.ofNullable(refreshStore.get(inMemoryKey));
    }

    @Override
    public void deleteTokenBy(Long memberId) {
        String inMemoryKey = getKey(String.valueOf(memberId));
        refreshStore.remove(inMemoryKey);
    }

    @Override
    public void deleteAll() {
        refreshStore.clear();
    }

    public RefreshTokens getTokens() {
        List<RefreshToken> verificationCodes = refreshStore.values()
                .stream()
                .toList();

        return new RefreshTokens(verificationCodes);
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
