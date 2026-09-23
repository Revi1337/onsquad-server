package revi1337.onsquad.crew_member.application.leaderboard;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.model.CrewActivityScoreSnapshot;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityScoreRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewLeaderboardUpdateService {

    private final CrewRankerRepository crewRankerRepository;
    private final CrewActivityScoreRepository crewActivityScoreRepository;

    @Transactional
    public void refreshLeaderboards(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        List<CrewRankerCandidate> candidates = crewActivityScoreRepository.fetchAggregatedRankedMembers(from, to, rankLimit);
        List<CrewActivityScoreSnapshot> snapshot = crewActivityScoreRepository.fetchSnapshot(from, to);
        crewActivityScoreRepository.subtractCountedWeight(snapshot);
        crewActivityScoreRepository.deleteZeroWeightRows();

        crewRankerRepository.deleteAllInBatch();
        crewRankerRepository.insertBatch(candidates);
        log.info("[LeaderboardUpdate] Leaderboard refreshed. ({} rankers)", candidates.size());
    }
}
