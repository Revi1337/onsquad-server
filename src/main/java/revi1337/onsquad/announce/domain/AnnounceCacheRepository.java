package revi1337.onsquad.announce.domain;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;

@RequiredArgsConstructor
@Repository
public class AnnounceCacheRepository {

    private final AnnounceQueryDslRepository announceQueryDslRepository;

    @Cacheable(cacheNames = CREW_ANNOUNCE, key = "'crew:' + #crewId + ':announce:' + #announceId", unless = "#result == null")
    public AnnounceInfoDomainDto getCachedByCrewIdAndIdAndMemberId(Long crewId, Long announceId, Long memberId) {
        return announceQueryDslRepository.fetchByCrewIdAndIdAndMemberId(crewId, announceId, memberId)
                .orElse(null);
    }

    @Cacheable(cacheNames = CREW_ANNOUNCES, key = "'crew:' + #crewId")
    public List<AnnounceInfoDomainDto> fetchCachedLimitedByCrewId(Long crewId, Long size) {
        return announceQueryDslRepository.fetchLimitedByCrewId(crewId, size);
    }
}
