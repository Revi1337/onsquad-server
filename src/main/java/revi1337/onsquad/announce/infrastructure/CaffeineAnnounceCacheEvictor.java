package revi1337.onsquad.announce.infrastructure;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;

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
 * Caffeine-specific implementation of {@link AnnounceCacheEvictor}. This implementation handles local in-memory caching by:
 * <ul>
 * <li><b>Direct Native Cache Access:</b> Unlike standard Spring Cache abstraction,
 * this implementation directly accesses the underlying {@code com.github.benmanes.caffeine.cache.Cache}
 * to perform key scanning and regex-based matching.</li>
 * <li><b>Regex-based Eviction:</b> Supports sophisticated pattern matching by iterating
 * over the native cache's key set and applying regular expressions.</li>
 * </ul>
 * <p>
 * <b>Performance Warning:</b>
 * As the number of entries in the {@code cacheName} increases, the overhead of scanning
 * the entire key set linearly increases (O(N)). This may impact application performance
 * if the cache size is extremely large. It is recommended to monitor the cache size
 * and use precise eviction (via {@link AnnounceReference}) whenever possible.
 *
 * @see AnnounceCacheEvictor
 */
@Component
public class CaffeineAnnounceCacheEvictor implements AnnounceCacheEvictor {

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
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        String key = String.join(Sign.COLON, "crew", crewId.toString(), "announce", announceId.toString());
        cache.evictIfPresent(key);
    }

    @Override
    public void evictAnnouncesByReferences(List<AnnounceReference> references) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        for (AnnounceReference reference : references) {
            String crewId = reference.crewId().toString();
            String announceId = reference.announceId().toString();
            String key = String.join(Sign.COLON, "crew", crewId, "announce", announceId);
            cache.evictIfPresent(key);
        }
    }

    @Override
    public void evictAnnouncesInCrew(Long crewId) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        List<String> computedKeys = getCaffeineCacheKeys(cache, List.of(crewId), "^crew:(%s):announce:.*");
        computedKeys.forEach(cache::evictIfPresent);
    }

    @Override
    public void evictAnnouncesInCrews(List<Long> crewIds) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCE);
        if (cache == null) {
            return;
        }

        List<String> computedKeys = getCaffeineCacheKeys(cache, crewIds, "^crew:(%s):announce:.*");
        computedKeys.forEach(cache::evictIfPresent);
    }

    @Override
    public void evictAnnounceListsByCrews(List<Long> crewIds) {
        Cache cache = cacheManager.getCache(CREW_ANNOUNCES);
        if (cache == null) {
            return;
        }

        crewIds.stream()
                .map(crewId -> String.join(Sign.COLON, CREW_ANNOUNCES, "crew", crewId.toString()))
                .forEach(cache::evictIfPresent);
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
