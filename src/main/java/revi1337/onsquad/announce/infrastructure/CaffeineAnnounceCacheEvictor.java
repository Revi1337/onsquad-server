package revi1337.onsquad.announce.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;
import revi1337.onsquad.announce.application.AnnounceCacheEvictor;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.common.constant.Sign;

/**
 * Caffeine-specific implementation of {@link AnnounceCacheEvictor} for local in-memory cache management. * <p>This implementation provides advanced eviction
 * capabilities for Caffeine by bypassing the standard Spring Cache decorative layer to perform the following:
 * <ul>
 * <li><b>Native Cache Interoperability:</b> Directly interacts with the underlying
 * {@code com.github.benmanes.caffeine.cache.Cache} to access the {@code asMap()} view
 * for sophisticated key management.</li>
 * <li><b>Regex-based Bulk Eviction:</b> Implements pattern-based removal by compiling
 * Java Regular Expressions to match and filter the in-memory key set.</li>
 * <li><b>Optimized Overloading:</b> Supports both single-crew and multi-crew bulk
 * invalidation through optimized regex grouping.</li>
 * </ul>
 *
 * <h2>Performance Considerations</h2>
 * <ul>
 * <li><b>Time Complexity:</b> Pattern-based methods ({@link #evictAnnounces(Long)} and
 * {@link #evictAnnounces(List)}) operate with <b>O(N)</b> complexity, where N is the total
 * number of entries in the specific cache.</li>
 * <li><b>Memory Overhead:</b> Iterating over the key set is performed in-process. While faster
 * than Redis network RTT, extremely large local caches may experience minor GC pressure during
 * high-frequency pattern evictions.</li>
 * <li><b>Best Practice:</b> Prefer {@link #evictAnnounce(Long, Long)} or
 * {@link #evictAnnouncesByReferences(List)} for O(1) targeting when specific IDs are available.</li>
 * </ul>
 *
 * @see AnnounceCacheEvictor
 * @see com.github.benmanes.caffeine.cache.Cache
 */
@Component
public class CaffeineAnnounceCacheEvictor implements AnnounceCacheEvictor {

    private static final String CREW_ANNOUNCE_KEY_FORMAT = "crew:%s:announce:%s";
    private static final String CREW_ANNOUNCES_KEY_FORMAT = "crew:%s:announces";

    private final CacheManager cacheManager;

    public CaffeineAnnounceCacheEvictor(@Qualifier("caffeineCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean supports(CacheManager cacheManager) {
        return cacheManager instanceof CaffeineCacheManager;
    }

    @Override
    public void evictAnnounce(Long crewId, Long announceId) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            String computedKey = String.format(CREW_ANNOUNCE_KEY_FORMAT, crewId, announceId);
            cache.evictIfPresent(computedKey);
        });
    }

    @Override
    public void evictAnnounces(Long crewId) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            List<String> computedKeys = getCaffeineCacheKeys(cache, List.of(crewId), "^crew:(%s):announce:.*");
            computedKeys.forEach(cache::evictIfPresent);
        });
    }

    @Override
    public void evictAnnounces(List<Long> crewIds) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            List<String> computedKeys = getCaffeineCacheKeys(cache, crewIds, "^crew:(%s):announce:.*");
            computedKeys.forEach(cache::evictIfPresent);
        });
    }

    @Override
    public void evictAnnouncesByReferences(List<AnnounceReference> references) {
        getCache(cacheManager, CREW_ANNOUNCE_CACHE_NAME).ifPresent(cache -> {
            for (AnnounceReference reference : references) {
                String computedKey = String.format(CREW_ANNOUNCE_KEY_FORMAT, reference.crewId(), reference.announceId());
                cache.evictIfPresent(computedKey);
            }
        });
    }

    @Override
    public void evictAnnounceLists(List<Long> crewIds) {
        getCache(cacheManager, CREW_ANNOUNCES_CACHE_NAME).ifPresent(cache -> {
            crewIds.stream()
                    .map(crewId -> String.format(CREW_ANNOUNCES_KEY_FORMAT, crewId))
                    .forEach(cache::evictIfPresent);
        });
    }

    private List<String> getCaffeineCacheKeys(Cache cache, List<Long> crewIds, String regexPattern) {
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();

        String crewIdGroup = crewIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(Sign.PIPE));

        Pattern pattern = Pattern.compile(String.format(regexPattern, crewIdGroup));
        List<String> matchKeys = new ArrayList<>();
        for (Object key : nativeCache.asMap().keySet()) {
            if (key instanceof String cacheKey && pattern.matcher(cacheKey).matches()) {
                matchKeys.add(cacheKey);
            }
        }

        return matchKeys;
    }
}
