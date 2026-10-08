package revi1337.onsquad.infrastructure.storage.redis;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Swallows cache errors so that a Redis failure degrades to a cache miss instead of failing the business method.
 * <p>
 * On get failure, the {@code CacheInterceptor} treats the result as a miss and invokes the original method.
 */
@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {

    private static final String CACHE_ERROR_LOG_FORMAT = "[Cache {} 실패: cache={}, key={}] {}: {}";
    private static final String CACHE_BLOCKED_LOG_FORMAT = "[Cache {} 차단: cache={}, key={}] {}";

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logError("get", exception, cache, key);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        logError("put", exception, cache, key);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        logError("evict", exception, cache, key);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        logError("clear", exception, cache, null);
    }

    private void logError(String operation, RuntimeException exception, Cache cache, Object key) {
        if (exception instanceof CallNotPermittedException) {
            log.warn(CACHE_BLOCKED_LOG_FORMAT, operation, cache.getName(), key, exception.getMessage());
            return;
        }
        log.error(CACHE_ERROR_LOG_FORMAT, operation, cache.getName(), key, exception.getClass().getSimpleName(), exception.getMessage(), exception);
    }
}
