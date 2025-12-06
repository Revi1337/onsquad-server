package revi1337.onsquad.crew.domain.repository;

import revi1337.onsquad.crew.domain.dto.CrewStatisticDomainDto;

public interface CrewStatisticCacheRepository {

    CrewStatisticDomainDto getStatisticById(Long crewId);

}
