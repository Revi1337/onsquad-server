package revi1337.onsquad.announce.application;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.common.constant.CacheConst;

/**
 * Interface for abstracting cache eviction logic for each CacheManager.
 * <p>
 * Standard Spring Cache abstraction has limitations in performing bulk evictions or pattern-based deletions. This interface provides a unified way to perform
 * optimized eviction operations depending on the underlying cache storage (e.g., Redis, Caffeine).
 *
 * @see AnnounceCacheEvictorFactory
 */
public interface AnnounceCacheEvictor {

    String CREW_ANNOUNCE_CACHE_NAME = CacheConst.CREW_ANNOUNCE;
    String CREW_ANNOUNCES_CACHE_NAME = CacheConst.CREW_ANNOUNCES;

    /**
     * Checks if this evictor supports the given CacheManager.
     *
     * @param cacheManager the CacheManager to check
     * @return {@code true} if supported, otherwise {@code false}
     */
    boolean supports(CacheManager cacheManager);

    /**
     * Evicts a single announcement cache entry.
     * <p>Targets a specific key using the combination of crewId and announceId.
     *
     * @param crewId     the ID of the crew
     * @param announceId the ID of the announcement to evict
     */
    void evictAnnounce(Long crewId, Long announceId);

    /**
     * Evicts all announcement caches for a specific crew.
     * <p>Implementation typically uses <b>pattern matching</b> (e.g., SCAN in Redis) to find
     * and remove all keys associated with the given crew.
     *
     * @param crewId the ID of the crew to clear
     */
    void evictAnnounces(Long crewId);

    /**
     * Evicts all announcement caches for multiple crews.
     * <p>A bulk version of {@link #evictAnnounces(Long)} that processes multiple crew IDs.
     *
     * @param crewIds a list of crew IDs to clear
     */
    void evictAnnounces(List<Long> crewIds);

    /**
     * Evicts specific announcement caches using precise references.
     * <p>Unlike pattern-based methods, this method targets <b>exact keys</b>.
     * It is generally more performant (O(1) per key) and safer than scanning.
     *
     * @param references a list of {@link AnnounceReference} containing exact IDs to evict
     */
    void evictAnnouncesByReferences(List<AnnounceReference> references);

    /**
     * Evicts only the collection (list) caches for the given crews.
     * <p>Note: This only clears the "list" views; individual announcement detail caches remain intact.
     *
     * @param crewIds a list of crew IDs whose list caches should be cleared
     */
    void evictAnnounceLists(List<Long> crewIds);

    default Optional<Cache> getCache(CacheManager cacheManager, String name) {
        return Optional.ofNullable(cacheManager.getCache(name));
    }
}
