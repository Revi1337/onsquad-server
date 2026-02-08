package revi1337.onsquad.announce.application;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.domain.model.AnnounceDetail;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.announce.domain.repository.AnnounceQueryDslRepository;
import revi1337.onsquad.announce.error.AnnounceBusinessException;
import revi1337.onsquad.announce.error.AnnounceErrorCode;

/**
 * Service responsible for orchestrating announcement cache operations using diverse strategies.
 * <p>
 * This service integrates Spring's declarative caching abstractions ({@link Cacheable}, {@link CachePut}) with explicit invalidation logic to handle complex
 * consistency requirements.
 *
 * <h2>Key Caching Patterns</h2>
 * <ul>
 * <li><b>Cache-Aside (Lazy Loading):</b> Utilized in {@code getAnnounce} and {@code getDefaultAnnounces}.
 * Entries are loaded into the cache only after a cache miss occurs in the primary database.</li>
 * <li><b>Active Refresh (Write-Through):</b> Utilized in {@code putAnnounce} and {@code putDefaultAnnounceList}.
 * Forces a database fetch to update the cache with the latest state, ensuring immediate consistency after modifications.</li>
 * <li><b>Explicit Eviction:</b> Delegated to {@link AnnounceCacheEvictor} to overcome the limitations of
 * standard Spring Cache, supporting both pinpoint and broad-range invalidation.</li>
 * </ul>
 *
 * <h2>Architectural Constraints</h2>
 * <ul>
 * <li><b>Manager Consistency:</b> The {@code cacheManager} identifier {@value #CACHE_MANAGER_NAME} used in
 * annotations <b>must</b> match the instance resolved by {@link AnnounceCacheEvictorFactory}.
 * Discrepancies will result in "silent failures" where eviction occurs on a different storage provider
 * than the one holding the active data.</li>
 * <li><b>Performance-Aware Invalidation:</b> Methods are categorized into <i>Exact Match</i>
 * (using specific references) and <i>Pattern Match</i> (scanning based on crew context).
 * Users should prefer reference-based eviction for high-throughput scenarios to avoid O(N) scan overhead.</li>
 * </ul>
 *
 * @see AnnounceCacheEvictor
 * @see AnnounceCacheEvictorFactory
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
        this.announceCacheEvictor = selectCacheEvictor(cacheManager, cacheEvictorFactory);
    }

    private AnnounceCacheEvictor selectCacheEvictor(CacheManager cacheManager, AnnounceCacheEvictorFactory cacheEvictorFactory) {
        return cacheEvictorFactory.findAvailableEvictor(cacheManager);
    }

    @Cacheable(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId", cacheManager = CACHE_MANAGER_NAME)
    public AnnounceDetail getAnnounce(Long crewId, Long announceId) {
        return announceQueryDslRepository.fetchByIdAndCrewId(announceId, crewId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }

    @Cacheable(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId", cacheManager = CACHE_MANAGER_NAME)
    public List<AnnounceDetail> getDefaultAnnounces(Long crewId) {
        return announceQueryDslRepository.fetchAllInDefaultByCrewId(crewId, DEFAULT_FETCH_SIZE);
    }

    @CachePut(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId", cacheManager = CACHE_MANAGER_NAME)
    public AnnounceDetail putAnnounce(Long crewId, Long announceId) {
        return announceQueryDslRepository.fetchByIdAndCrewId(announceId, crewId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }

    @CachePut(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId", cacheManager = CACHE_MANAGER_NAME)
    public List<AnnounceDetail> putDefaultAnnounceList(Long crewId) {
        return announceQueryDslRepository.fetchAllInDefaultByCrewId(crewId, DEFAULT_FETCH_SIZE);
    }

    public void evictAnnounce(Long crewId, Long announceId) {
        announceCacheEvictor.evictAnnounce(crewId, announceId);
    }

    public void evictAnnounces(Long crewId) {
        announceCacheEvictor.evictAnnounces(crewId);
    }

    public void evictAnnounces(List<Long> crewIds) {
        announceCacheEvictor.evictAnnounces(crewIds);
    }

    public void evictAnnouncesByReferences(List<AnnounceReference> references) {
        announceCacheEvictor.evictAnnouncesByReferences(references);
    }

    public void evictAnnounceLists(List<Long> crewIds) {
        announceCacheEvictor.evictAnnounceLists(crewIds);
    }
}
