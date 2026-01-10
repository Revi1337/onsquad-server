package revi1337.onsquad.announce.infrastructure;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.announce.application.AnnounceCacheEvictor;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.infrastructure.redis.RedisCacheEvictor;

/**
 * Redis-specific implementation of {@link AnnounceCacheEvictor}. This implementation optimizes eviction performance for distributed environments by:
 * <ul>
 * <li><b>Bulk Deletion:</b> Aggregates multiple cache keys into a single request
 * to minimize network round-trips (RTT).</li>
 * <li><b>Asynchronous Invalidation:</b> Utilizes the {@code UNLINK} command for bulk and
 * pattern-based evictions. Unlike {@code DEL}, {@code UNLINK} reclaims memory in a
 * background thread, ensuring the Redis event loop remains non-blocking even when
 * processing large datasets.</li>
 * <li><b>Synchronous Precision:</b> Employs {@code DEL} for single-item evictions
 * where the payload is guaranteed to be small, ensuring immediate memory reclamation
 * with minimal overhead.</li>
 * </ul>
 *
 * @see AnnounceCacheEvictor
 */
@Component
public class RedisAnnounceCacheEvictor implements AnnounceCacheEvictor {

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
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        String key = String.join(Sign.COLON, CREW_ANNOUNCE, "crew", crewId.toString(), "announce", announceId.toString());
        String computedKey = String.format(CacheFormat.SIMPLE, key);
        RedisCacheEvictor.unlinkKey(stringRedisTemplate, computedKey);
    }

    @Override
    public void evictAnnouncesByReferences(List<AnnounceReference> references) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        List<String> computedKeys = new ArrayList<>();
        for (AnnounceReference reference : references) {
            String crewId = reference.crewId().toString();
            String announceId = reference.announceId().toString();
            String key = String.join(Sign.COLON, CREW_ANNOUNCE, "crew", crewId, "announce", announceId);
            String computedKey = String.format(CacheFormat.SIMPLE, key);
            computedKeys.add(computedKey);
        }

        RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys);
    }

    @Override
    public void evictAnnouncesInCrew(Long crewId) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        String key = String.join(Sign.COLON, CREW_ANNOUNCE, "crew", crewId.toString(), "announce", Sign.ASTERISK);
        String pattern = String.format(CacheFormat.SIMPLE, key);

        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, pattern);
    }

    @Override
    public void evictAnnouncesInCrews(List<Long> crewIds) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        List<String> patterns = crewIds.stream()
                .map(crewId -> String.join(Sign.COLON, CREW_ANNOUNCE, "crew", crewId.toString(), "announce", Sign.ASTERISK))
                .map(key -> String.format(CacheFormat.SIMPLE, key))
                .toList();

        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, patterns);
    }

    @Override
    public void evictAnnounceListsByCrews(List<Long> crewIds) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCES);
        if (cache == null) {
            return;
        }

        List<String> computedKeys = crewIds.stream()
                .map(crewId -> String.join(Sign.COLON, CREW_ANNOUNCES, "crew", crewId.toString()))
                .map(key -> String.format(CacheFormat.SIMPLE, key))
                .toList();

        RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys);
    }
}
