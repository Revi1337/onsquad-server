package revi1337.onsquad.announce.infrastructure;

import java.util.List;
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
 * @see AnnounceCacheEvictor
 * @see RedisCacheEvictor
 * @see revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils
 */
@Component
public class RedisAnnounceCacheEvictor implements AnnounceCacheEvictor {

    private static final String CREW_ANNOUNCE_KEY_FORMAT = String.join(Sign.COLON, CREW_ANNOUNCE_CACHE_NAME, "crew:%s:announce:%s");
    private static final String CREW_ANNOUNCES_KEY_FORMAT = String.join(Sign.COLON, CREW_ANNOUNCES_CACHE_NAME, "crew:%s:announces");
    private static final String CREW_ANNOUNCE_KEY_PATTERN = String.join(Sign.COLON, CREW_ANNOUNCE_CACHE_NAME, "crew:%s:announce:*");

    private final CacheManager cacheManager;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisAnnounceCacheEvictor(@Qualifier("redisCacheManager") CacheManager cacheManager, StringRedisTemplate stringRedisTemplate) {
        this.cacheManager = cacheManager;
        this.stringRedisTemplate = stringRedisTemplate;
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
            RedisCacheEvictor.unlinkKey(stringRedisTemplate, computedKey);
        });
    }

    @Override
    public void evictAnnounces(Long crewId) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            String pattern = String.format(CREW_ANNOUNCE_KEY_PATTERN, crewId);
            String computedPattern = String.format(CacheFormat.SIMPLE, pattern);
            RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, computedPattern);
        });
    }

    @Override
    public void evictAnnounces(List<Long> crewIds) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            List<String> computedPatterns = crewIds.stream()
                    .map(crewId -> String.format(CREW_ANNOUNCE_KEY_PATTERN, crewId))
                    .map(pattern -> String.format(CacheFormat.SIMPLE, pattern))
                    .toList();

            RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, computedPatterns);
        });
    }

    @Override
    public void evictAnnouncesByReferences(List<AnnounceReference> references) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            List<String> computedKeys = references.stream()
                    .map(reference -> String.format(CREW_ANNOUNCE_KEY_FORMAT, reference.crewId(), reference.announceId()))
                    .map(key -> String.format(CacheFormat.SIMPLE, key))
                    .toList();

            RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys);
        });
    }

    @Override
    public void evictAnnounceLists(List<Long> crewIds) {
        getCache(cacheManager, CREW_ANNOUNCES_CACHE_NAME).ifPresent(cache -> {
            List<String> computedKeys = crewIds.stream()
                    .map(crewId -> String.format(CREW_ANNOUNCES_KEY_FORMAT, crewId))
                    .map(key -> String.format(CacheFormat.SIMPLE, key))
                    .toList();

            RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys);
        });
    }
}
