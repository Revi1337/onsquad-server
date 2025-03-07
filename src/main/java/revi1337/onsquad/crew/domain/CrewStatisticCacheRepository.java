package revi1337.onsquad.crew.domain;

import revi1337.onsquad.crew.domain.dto.CrewStatisticDomainDto;

public interface CrewStatisticCacheRepository {

    CrewStatisticDomainDto getStatisticById(Long crewId);

}
