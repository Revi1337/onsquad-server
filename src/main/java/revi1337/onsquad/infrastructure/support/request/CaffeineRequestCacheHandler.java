package revi1337.onsquad.infrastructure.support.request;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.aspect.RequestCacheHandler;
import revi1337.onsquad.common.aspect.RequestCacheHandlerChain;
import revi1337.onsquad.infrastructure.storage.caffeine.TimedEntry;

/**
 * DefaultRequestCacheHandler For RequestCacheHandlerExecutionChain
 *
 * @see RequestCacheHandlerChain
 */
@Order(2)
@Component
public final class CaffeineRequestCacheHandler implements RequestCacheHandler {

    private static final int MAX_CACHE_SIZE = 10_000;

    private static final Expiry<String, TimedEntry<String>> EXPIRY = new Expiry<>() {
        @Override
        public long expireAfterCreate(String key, TimedEntry<String> entry, long currentTime) {
            return entry.ttlNanos();
        }

        @Override
        public long expireAfterUpdate(String key, TimedEntry<String> entry, long currentTime, long currentDuration) {
            return entry.ttlNanos();
        }

        @Override
        public long expireAfterRead(String key, TimedEntry<String> entry, long currentTime, long currentDuration) {
            return currentDuration;
        }
    };

    private static final Cache<String, TimedEntry<String>> REQUEST_CACHE =
            Caffeine.newBuilder()
                    .maximumSize(MAX_CACHE_SIZE)
                    .expireAfter(EXPIRY)
                    .build();

    @Override
    public Boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        TimedEntry<String> existing = REQUEST_CACHE.getIfPresent(key);
        if (existing == null) {
            REQUEST_CACHE.put(key, new TimedEntry<>(value, unit.toNanos(timeout)));
            return true;
        }

        return false;
    }
}
