package revi1337.onsquad.infrastructure.storage.redis;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.Map;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * {@link RedisCacheManager} whose caches are decorated with {@link CircuitBreakerCache}.
 * <p>
 * It must stay a {@link RedisCacheManager} subtype, because consumers such as {@code RedisAnnounceCacheEvictor} select their strategy by
 * {@code instanceof RedisCacheManager}.
 */
public class CircuitBreakerRedisCacheManager extends RedisCacheManager {

    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerRedisCacheManager(
            RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfiguration,
            Map<String, RedisCacheConfiguration> initialCacheConfigurations,
            CircuitBreaker circuitBreaker
    ) {
        super(cacheWriter, defaultCacheConfiguration, true, initialCacheConfigurations);
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    protected Cache decorateCache(Cache cache) {
        return new CircuitBreakerCache(super.decorateCache(cache), circuitBreaker);
    }
}
