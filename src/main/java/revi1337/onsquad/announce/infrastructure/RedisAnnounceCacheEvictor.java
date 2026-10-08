package revi1337.onsquad.announce.infrastructure;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.announce.application.AnnounceCacheEvictor;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

/**
 * Redis-specific implementation of {@link AnnounceCacheEvictor} designed for high-performance and non-blocking cache invalidation in distributed environments.
 *
 * <p>This implementation addresses common Redis performance pitfalls by employing the following strategies:
 * <ul>
 * <li><b>Asynchronous Memory Reclamation:</b> Utilizes the {@code UNLINK} command instead of {@code DEL}.
 * This offloads the heavy lifting of memory deallocation to a background thread, preventing the Redis
 * main event loop from blocking—critical for large collection evictions.</li>
 * <li><b>Safe Key Discovery:</b> For group-based evictions (e.g., all announcements in a crew),
 * it uses a cursor-based {@code SCAN} approach via {@link revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils}. This avoids the
 * "stop-the-world" effect of the {@code KEYS} command.</li>
 * <li><b>Minimizing RTT:</b> Aggregates multiple keys into bulk {@code UNLINK} requests,
 * significantly reducing network round-trip time (RTT) overhead.</li>
 * </ul>
 *
 * <h2>Execution Context</h2>
 * This evictor expects a {@link RedisCacheManager} and operates primarily through
 * {@link StringRedisTemplate} to ensure consistent key serialization matching the
 * {@link CacheFormat#SIMPLE} pattern.
 *
 * <h2>Failure Isolation</h2>
 * {@link RedisCacheEvictor} propagates Redis failures as-is. This adapter is the one that knows eviction is best-effort, so it runs every Redis call through the
 * {@code redisCacheCircuitBreaker} circuit breaker and degrades failures to a log entry instead of failing the business method. Failures must reach the
 * circuit breaker, which is why the utilities never swallow them.
 *
 * @see AnnounceCacheEvictor
 * @see RedisCacheEvictor
 * @see revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils
 */
@Slf4j
@Component
public class RedisAnnounceCacheEvictor implements AnnounceCacheEvictor {

    private static final String REDIS_CACHE_CIRCUIT_BREAKER_NAME = "redisCacheCircuitBreaker";
    private static final String EVICT_ERROR_LOG_FORMAT = "[Announce Cache evict 실패] {}: {}";
    private static final String EVICT_BLOCKED_LOG_FORMAT = "[Announce Cache evict 차단] {}";
    private static final String CREW_ANNOUNCE_KEY_FORMAT = String.join(Sign.COLON, CREW_ANNOUNCE_CACHE_NAME, "crew:%s:announce:%s");
    private static final String CREW_ANNOUNCES_KEY_FORMAT = String.join(Sign.COLON, CREW_ANNOUNCES_CACHE_NAME, "crew:%s");
    private static final String CREW_ANNOUNCE_KEY_PATTERN = String.join(Sign.COLON, CREW_ANNOUNCE_CACHE_NAME, "crew:%s:announce:*");

    private final CacheManager cacheManager;
    private final StringRedisTemplate stringRedisTemplate;
    private final CircuitBreaker circuitBreaker;

    public RedisAnnounceCacheEvictor(
            @Qualifier("redisCacheManager") CacheManager cacheManager,
            StringRedisTemplate stringRedisTemplate,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.cacheManager = cacheManager;
        this.stringRedisTemplate = stringRedisTemplate;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(REDIS_CACHE_CIRCUIT_BREAKER_NAME);
    }

    @Override
    public boolean supports(CacheManager cacheManager) {
        return cacheManager instanceof RedisCacheManager;
    }

    @Override
    public void evictAnnounce(Long crewId, Long announceId) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            String key = String.format(CREW_ANNOUNCE_KEY_FORMAT, crewId, announceId);
            String computedKey = String.format(CacheFormat.SIMPLE, key);
            evictQuietly(() -> RedisCacheEvictor.unlinkKey(stringRedisTemplate, computedKey));
        });
    }

    @Override
    public void evictAnnounces(Long crewId) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            String pattern = String.format(CREW_ANNOUNCE_KEY_PATTERN, crewId);
            String computedPattern = String.format(CacheFormat.SIMPLE, pattern);
            evictQuietly(() -> RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, computedPattern));
        });
    }

    @Override
    public void evictAnnounces(List<Long> crewIds) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            List<String> computedPatterns = crewIds.stream()
                    .map(crewId -> String.format(CREW_ANNOUNCE_KEY_PATTERN, crewId))
                    .map(pattern -> String.format(CacheFormat.SIMPLE, pattern))
                    .toList();

            evictQuietly(() -> RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, computedPatterns));
        });
    }

    @Override
    public void evictAnnouncesByReferences(List<AnnounceReference> references) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            List<String> computedKeys = references.stream()
                    .map(reference -> String.format(CREW_ANNOUNCE_KEY_FORMAT, reference.crewId(), reference.announceId()))
                    .map(key -> String.format(CacheFormat.SIMPLE, key))
                    .toList();

            evictQuietly(() -> RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys));
        });
    }

    @Override
    public void evictAnnounceLists(List<Long> crewIds) {
        getCache(cacheManager, CREW_ANNOUNCES_CACHE_NAME).ifPresent(cache -> {
            List<String> computedKeys = crewIds.stream()
                    .map(crewId -> String.format(CREW_ANNOUNCES_KEY_FORMAT, crewId))
                    .map(key -> String.format(CacheFormat.SIMPLE, key))
                    .toList();

            evictQuietly(() -> RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys));
        });
    }

    private void evictQuietly(Runnable evict) {
        try {
            circuitBreaker.executeRunnable(evict);
        } catch (CallNotPermittedException e) {
            log.warn(EVICT_BLOCKED_LOG_FORMAT, e.getMessage());
        } catch (RuntimeException e) {
            log.error(EVICT_ERROR_LOG_FORMAT, e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }
}
