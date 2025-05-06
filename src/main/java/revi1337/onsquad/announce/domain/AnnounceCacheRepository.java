package revi1337.onsquad.announce.domain;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.NOT_FOUND;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;

@RequiredArgsConstructor
@Repository
public class AnnounceCacheRepository {

    private final AnnounceQueryDslRepository announceQueryDslRepository;

    @Cacheable(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId")
    public AnnounceDomainDto fetchCacheByCrewIdAndId(Long crewId, Long announceId) {
        return announceQueryDslRepository.fetchByCrewIdAndId(crewId, announceId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFoundById(NOT_FOUND, announceId));
    }

    @Cacheable(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId")
    public List<AnnounceDomainDto> fetchAllCacheInDefaultByCrewId(Long crewId) {
        return announceQueryDslRepository.fetchAllInDefaultByCrewId(crewId);
    }
}
