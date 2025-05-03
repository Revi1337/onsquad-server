package revi1337.onsquad.auth.repository.token;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.auth.model.token.RefreshToken;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@Repository
public class ExpiringMapTokenRepository implements TokenRepository {

    private static final int MAX_CACHE_SIZE = 10_000;
    private static final ExpiringMap<String, RefreshToken> REFRESH_CACHE = ExpiringMap.builder()
            .maxSize(MAX_CACHE_SIZE)
            .variableExpiration()
            .build();

    @Override
    public void save(RefreshToken refreshToken, Long memberId, Duration expired) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        REFRESH_CACHE.put(cacheName, refreshToken, ExpirationPolicy.CREATED, expired.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<RefreshToken> findBy(Long memberId) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        return Optional.ofNullable(REFRESH_CACHE.get(cacheName));
    }

    @Override
    public void deleteBy(Long memberId) {
        String cacheIdentifier = "user:" + memberId;
        String cacheName = String.format(CacheFormat.COMPLEX, CacheConst.REFRESH_TOKEN, cacheIdentifier);
        REFRESH_CACHE.remove(cacheName);
    }

    @Override
    public void deleteAll() {
        REFRESH_CACHE.clear();
    }
}
