package revi1337.onsquad.announce.domain;

import static java.util.concurrent.TimeUnit.HOURS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.common.aspect.RedisCache;

@RequiredArgsConstructor
@Repository
public class AnnounceCacheRepository {

    private final AnnounceQueryDslRepository announceQueryDslRepository;

    @RedisCache(name = "crew-announce", key = "'crew:' + #crewId + ':announce:' + #announceId", unit = HOURS)
    public AnnounceInfoDomainDto getCachedByCrewIdAndIdAndMemberId(Long crewId, Long announceId, Long memberId) {
        return announceQueryDslRepository.fetchByCrewIdAndIdAndMemberId(crewId, announceId, memberId)
                .orElse(null);
    }

    @RedisCache(name = "crew-announces", key = "'crew:' + #crewId", unit = HOURS, cacheEmptyCollection = true)
    public List<AnnounceInfoDomainDto> fetchCachedLimitedByCrewId(Long crewId, Long size) {
        return announceQueryDslRepository.fetchLimitedByCrewId(crewId, size);
    }
}
