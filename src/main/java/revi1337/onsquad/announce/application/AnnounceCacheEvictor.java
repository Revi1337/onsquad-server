package revi1337.onsquad.announce.application;

import java.util.List;
import org.springframework.cache.CacheManager;
import revi1337.onsquad.announce.domain.result.AnnounceReference;

/**
 * Interface for abstracting cache eviction logic for each CacheManager.
 * <p>
 * Standard Spring Cache abstraction has limitations in performing bulk evictions or pattern-based deletions. This interface provides a unified way to perform
 * optimized eviction operations depending on the underlying cache storage (e.g., Redis, Caffeine).
 *
 * @see AnnounceCacheEvictorFactory
 */
public interface AnnounceCacheEvictor {

    /**
     * Checks if this evictor supports the given CacheManager.
     *
     * @param cacheManager the CacheManager to check
     * @return {@code true} if supported, otherwise {@code false}
     */
    boolean supports(CacheManager cacheManager);

    /**
     * Evicts a single announcement cache.
     *
     * @param crewId     the ID of the crew
     * @param announceId the ID of the announcement to evict
     */
    void evictAnnounce(Long crewId, Long announceId);

    /**
     * Evicts multiple specific announcement caches using provided references.
     * <p>
     * This method is optimized for accuracy and performance by targeting exact keys (O(1) per key) rather than using pattern matching.
     *
     * @param references a list of {@link AnnounceReference} to evict
     */
    void evictAnnouncesByReferences(List<AnnounceReference> references);

    /**
     * Evicts all announcement caches within a specific crew using pattern matching.
     *
     * @param crewId the ID of the crew whose announcement caches should be cleared
     */
    void evictAnnouncesInCrew(Long crewId);

    /**
     * Evicts all announcement caches for multiple crews using bulk pattern matching.
     *
     * @param crewIds a list of crew IDs whose announcement caches should be cleared
     */
    void evictAnnouncesInCrews(List<Long> crewIds);

    /**
     * Evicts only the announcement list caches for the given crews. Individual announcement item caches are not affected.
     *
     * @param crewIds a list of crew IDs whose list caches should be evicted
     */
    void evictAnnounceListsByCrews(List<Long> crewIds);

}
