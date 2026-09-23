package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.crew_member.domain.model.CrewActivityScoreSnapshot;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;

public interface CrewActivityScoreRepository {

    void upsertScore(Long crewId, Long memberId, int weight, LocalDateTime lastActivityAt);

    List<CrewRankerCandidate> fetchAggregatedRankedMembers(LocalDateTime from, LocalDateTime to, Integer rankLimit);

    List<CrewActivityScoreSnapshot> fetchSnapshot(LocalDateTime from, LocalDateTime to);

    void subtractCountedWeight(List<CrewActivityScoreSnapshot> snapshot);

    void deleteZeroWeightRows();

}
