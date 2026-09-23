package revi1337.onsquad.crew_member.domain.repository.rank;

import java.util.List;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

public interface CrewRankerRepository {

    List<CrewRanker> findAll();

    List<CrewRanker> findAllByCrewId(Long crewId);

    void insertBatch(List<CrewRankerCandidate> candidates);

    void deleteAllInBatch();

    void swapSnapshot(List<CrewRankerCandidate> candidates);

}
