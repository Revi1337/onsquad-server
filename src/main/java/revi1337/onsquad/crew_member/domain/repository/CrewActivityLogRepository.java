package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityLog;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

public interface CrewActivityLogRepository {

    CrewActivityLog save(CrewActivityLog crewActivityLog);

    List<CrewRankerCandidate> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit);

    void deleteByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

}
