package revi1337.onsquad.backup.crew.domain;

import java.util.List;

public interface CrewTopCacheRepository {

    void deleteAllInBatch();

    void batchInsertCrewTop(List<CrewTopCache> crewTopCaches);

}
