package revi1337.onsquad.backup.crew.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
