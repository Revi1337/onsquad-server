package revi1337.onsquad.crew_member.domain.repository.rank;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;

public interface CrewRankerRepository {

    List<CrewRanker> findAll();

    List<CrewRanker> findAllByCrewId(Long crewId);

    List<CrewRankerDetail> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit);

    boolean exists();

    void insertBatch(List<CrewRanker> rankedMembers);

    void deleteAllInBatch();

    void truncate();

}
