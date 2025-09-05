package revi1337.onsquad.common.aspect;

import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;

/**
 * DefaultRequestCacheHandler For RequestCacheHandlerExecutionChain
 *
 * @see RequestCacheHandlerExecutionChain
 */
@Component
public final class ExpiringMapRequestCacheHandler implements RequestCacheHandler {

    private static final int MAX_CACHE_SIZE = 10_000;
    private static final ExpiringMap<String, String> REQUEST_CACHE = ExpiringMap.builder()
            .maxSize(MAX_CACHE_SIZE)
            .variableExpiration()
            .build();

    @Override
    public Boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        String cacheValue = REQUEST_CACHE.get(key);
        if (cacheValue == null) {
            REQUEST_CACHE.put(key, value, ExpirationPolicy.CREATED, timeout, unit);
            return true;
        }

        return false;
    }
}
