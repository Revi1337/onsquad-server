package revi1337.onsquad.backup.crew.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CrewTopCacheCacheRepositoryImpl implements CrewTopCacheRepository {

    private final CrewTopCacheJpaRepository crewTopCacheJpaRepository;
    private final CrewTopCacheJdbcRepository crewTopCacheJdbcRepository;

    @Override
    public void deleteAllInBatch() {
        crewTopCacheJpaRepository.deleteAllInBatch();
    }

    @Override
    public void batchInsertCrewTop(List<CrewTopCache> crewTopCaches) {
        crewTopCacheJdbcRepository.batchInsertCrewTop(crewTopCaches);
    }
}
