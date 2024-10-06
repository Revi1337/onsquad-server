package revi1337.onsquad.common.aspect;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

public class ExpiredMapRequestCacheHandler implements RequestCacheHandler {

    private static final ExpiringMap<String, String> REQUEST_CACHE = ExpiringMap.builder()
            .maxSize(10000)
            .variableExpiration()
            .build();

    @Override
    public boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        String cacheValue = REQUEST_CACHE.get(key);
        if (cacheValue == null) {
            REQUEST_CACHE.put(key, value, ExpirationPolicy.CREATED, timeout, unit);
            return true;
        }

        return false;
    }
}
