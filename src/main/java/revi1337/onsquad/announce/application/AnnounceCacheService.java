package revi1337.onsquad.announce.application;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.domain.repository.AnnounceQueryDslRepository;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.announce.error.AnnounceBusinessException;
import revi1337.onsquad.announce.error.AnnounceErrorCode;

/**
 * Service responsible for managing announcement caches with different strategies.
 * <p>
 * This service provides methods for both passive caching (Cache-Aside) and active cache synchronization (Eager Refresh/Write-Through).
 * <ul>
 * <li><b>getAnnounce / getDefaultAnnounces:</b> Implements the <i>Cache-Aside</i> pattern.
 * Fetches data from the cache first, or populates it from the database on a cache miss.</li>
 * <li><b>putAnnounce / putDefaultAnnounceList:</b> Implements <i>Active Refresh</i>.
 * Forces a database query and updates the cache with the latest data, typically used after data modifications.</li>
 * <li><b>evictAnnounce:</b> Explicitly removes an entry from the cache, primarily used during deletion.</li>
 * <li><b>evictAnnouncesByReferences / evictAnnounceListsByCrews:</b> Implements <i>Batch Eviction</i>.
 * Optimizes cache invalidation by grouping multiple keys, supporting O(1) targeting or pattern-based matching.</li>
 * </ul>
 * <p>
 * <b>Important Constraint:</b>
 * The {@code cacheManager} value specified in {@link Cacheable} and {@link CachePut} annotations
 * <b>MUST</b> be consistent with the {@code CacheManager} instance injected via the constructor.
 * Since {@link AnnounceCacheEvictor} is resolved based on the injected {@code CacheManager},
 * any inconsistency will lead to eviction failures where the evictor attempts to clear
 * a different cache storage than the one being populated.
 * </p>
 * <p>
 * The eviction logic is delegated to {@link AnnounceCacheEvictor}, which is customized to overcome
 * the limitations of standard Spring Cache Managers. It provides high-performance bulk operations
 * (e.g., Redis UNLINK) and complex pattern matching (e.g., Regex scanning in Caffeine)
 * to ensure consistency across distributed and local environments.
 */
@Service
public class AnnounceCacheService {

    private static final String CACHE_MANAGER_NAME = "redisCacheManager";
    private static final int DEFAULT_FETCH_SIZE = 4;

    private final AnnounceQueryDslRepository announceQueryDslRepository;
    private final AnnounceCacheEvictor announceCacheEvictor;

    public AnnounceCacheService(
            AnnounceQueryDslRepository announceQueryDslRepository,
            @Qualifier(CACHE_MANAGER_NAME) CacheManager cacheManager,
            AnnounceCacheEvictorFactory cacheEvictorFactory
    ) {
        this.announceQueryDslRepository = announceQueryDslRepository;
        this.announceCacheEvictor = initializeEvictor(cacheManager, cacheEvictorFactory);
    }

    private AnnounceCacheEvictor initializeEvictor(CacheManager cacheManager, AnnounceCacheEvictorFactory cacheEvictorFactory) {
        return cacheEvictorFactory.findAvailableEvictor(cacheManager);
    }

    @Cacheable(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId", cacheManager = CACHE_MANAGER_NAME)
    public AnnounceResult getAnnounce(Long crewId, Long announceId) {
        return announceQueryDslRepository.fetchByCrewIdAndId(crewId, announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }

    @Cacheable(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId", cacheManager = CACHE_MANAGER_NAME)
    public List<AnnounceResult> getDefaultAnnounces(Long crewId) {
        return announceQueryDslRepository.fetchAllInDefaultByCrewId(crewId, DEFAULT_FETCH_SIZE);
    }

    @CachePut(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId", cacheManager = CACHE_MANAGER_NAME)
    public AnnounceResult putAnnounce(Long crewId, Long announceId) {
        return announceQueryDslRepository.fetchByCrewIdAndId(crewId, announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }

    @CachePut(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId", cacheManager = CACHE_MANAGER_NAME)
    public List<AnnounceResult> putDefaultAnnounceList(Long crewId) {
        return announceQueryDslRepository.fetchAllInDefaultByCrewId(crewId, DEFAULT_FETCH_SIZE);
    }

    public void evictAnnounce(Long crewId, Long announceId) {
        announceCacheEvictor.evictAnnounce(crewId, announceId);
    }

    public void evictAnnouncesByReferences(List<AnnounceReference> references) {
        announceCacheEvictor.evictAnnouncesByReferences(references);
    }

    public void evictAnnouncesInCrews(List<Long> crewIds) {
        announceCacheEvictor.evictAnnouncesInCrews(crewIds);
    }

    public void evictAnnounceListsByCrews(List<Long> crewIds) {
        announceCacheEvictor.evictAnnounceListsByCrews(crewIds);
    }
}
