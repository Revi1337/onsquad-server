package revi1337.onsquad.announce.application;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.domain.repository.AnnounceQueryDslRepository;
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
 * <li><b>putAnnounceCache / putDefaultAnnounceListCache:</b> Implements <i>Active Refresh</i>.
 * Forces a database query and updates the cache with the latest data, typically used after data modifications.</li>
 * <li><b>evictAnnounceCache:</b> Explicitly removes an entry from the cache, primarily used during deletion.</li>
 * </ul>
 */
@RequiredArgsConstructor
@Service
public class AnnounceCacheService {

    private final AnnounceQueryDslRepository announceQueryDslRepository;

    @Cacheable(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId")
    public AnnounceResult getAnnounce(Long crewId, Long announceId) {
        return announceQueryDslRepository.fetchByCrewIdAndId(crewId, announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }

    @Cacheable(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId")
    public List<AnnounceResult> getDefaultAnnounces(Long crewId) {
        return announceQueryDslRepository.fetchAllInDefaultByCrewId(crewId);
    }

    @CachePut(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId")
    public AnnounceResult putAnnounceCache(Long crewId, Long announceId) {
        return announceQueryDslRepository.fetchByCrewIdAndId(crewId, announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFound(AnnounceErrorCode.NOT_FOUND));
    }

    @CachePut(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId")
    public List<AnnounceResult> putDefaultAnnounceListCache(Long crewId) {
        return announceQueryDslRepository.fetchAllInDefaultByCrewId(crewId);
    }

    @CacheEvict(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId")
    public void evictAnnounceCache(Long crewId, Long announceId) {
    }
}
