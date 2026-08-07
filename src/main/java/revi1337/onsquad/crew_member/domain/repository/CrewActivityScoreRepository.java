package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

public interface CrewActivityScoreRepository {

    void upsertScore(Long crewId, Long memberId, int weight, LocalDateTime lastActivityAt);

    List<CrewRankerCandidate> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit);

    void deleteByLastActivityAtBetween(LocalDateTime from, LocalDateTime to);

}
