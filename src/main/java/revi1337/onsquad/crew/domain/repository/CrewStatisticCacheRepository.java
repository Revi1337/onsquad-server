package revi1337.onsquad.crew.domain.repository;

import revi1337.onsquad.crew.domain.result.CrewStatisticResult;

public interface CrewStatisticCacheRepository {

    CrewStatisticResult getStatisticById(Long crewId);

}
