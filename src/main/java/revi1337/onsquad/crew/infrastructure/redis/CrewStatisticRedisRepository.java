package revi1337.onsquad.crew.infrastructure.redis;

import static revi1337.onsquad.common.constant.CacheConst.CREW_STATISTIC;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.dto.CrewStatisticDomainDto;
import revi1337.onsquad.crew.domain.repository.CrewStatisticCacheRepository;
import revi1337.onsquad.crew.domain.repository.CrewStatisticQueryDslRepository;

@RequiredArgsConstructor
@Repository
public class CrewStatisticRedisRepository implements CrewStatisticCacheRepository {

    private final CrewStatisticQueryDslRepository crewStatisticQueryDslRepository;

    @Cacheable(cacheNames = CREW_STATISTIC, key = "'crew:' + #crewId")
    @Override
    public CrewStatisticDomainDto getStatisticById(Long crewId) {
        return crewStatisticQueryDslRepository.getStatisticById(crewId);
    }
}
