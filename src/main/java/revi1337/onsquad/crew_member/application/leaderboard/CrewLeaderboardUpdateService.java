package revi1337.onsquad.crew_member.application.leaderboard;

import static revi1337.onsquad.crew_member.config.CrewLeaderboardProperties.OVER_FETCH_LIMIT;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.domain.model.CrewActivityScoreSnapshot;
import revi1337.onsquad.crew_member.domain.model.CrewMembership;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewActivityScoreRepository;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewLeaderboardUpdateService {

    private final CrewRankerRepository crewRankerRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewActivityScoreRepository crewActivityScoreRepository;

    public void refreshLeaderboards(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        List<CrewActivityScoreSnapshot> snapshot = crewActivityScoreRepository.fetchSnapshot(from, to);
        List<CrewRankerCandidate> overFetchedCandidates = crewActivityScoreRepository.fetchAggregatedRankedMembers(from, to, OVER_FETCH_LIMIT);
        Set<CrewMembership> candidateMemberships = extractMemberships(overFetchedCandidates);
        Set<CrewMembership> activeMemberships = crewMemberRepository.fetchActiveMemberships(candidateMemberships);

        List<CrewRankerCandidate> candidates = reselectTopRankers(overFetchedCandidates, activeMemberships, rankLimit);

        crewRankerRepository.swapSnapshot(candidates);
        crewActivityScoreRepository.subtractCountedWeight(snapshot);
        crewActivityScoreRepository.deleteZeroWeightRows();
        log.info(
                "[LeaderboardUpdate] Leaderboard refreshed. ({} over-fetched -> {} rankers)",
                overFetchedCandidates.size(), candidates.size()
        );
    }

    private Set<CrewMembership> extractMemberships(List<CrewRankerCandidate> candidates) {
        return candidates.stream()
                .map(candidate -> new CrewMembership(candidate.crewId(), candidate.memberId()))
                .collect(Collectors.toSet());
    }

    private List<CrewRankerCandidate> reselectTopRankers(
            List<CrewRankerCandidate> overFetchedCandidates, Set<CrewMembership> activeMemberships, Integer rankLimit
    ) {
        return overFetchedCandidates.stream()
                .filter(candidate -> activeMemberships.contains(new CrewMembership(candidate.crewId(), candidate.memberId())))
                .collect(Collectors.groupingBy(CrewRankerCandidate::crewId, LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .flatMap(rankedCandidatesInCrew -> reassignRanks(rankedCandidatesInCrew, rankLimit).stream())
                .toList();
    }

    private List<CrewRankerCandidate> reassignRanks(List<CrewRankerCandidate> orderedCandidates, int rankLimit) {
        List<CrewRankerCandidate> reranked = new ArrayList<>();
        for (int i = 0; i < orderedCandidates.size() && i < rankLimit; i++) {
            reranked.add(orderedCandidates.get(i).withRank(i + 1));
        }
        return reranked;
    }
}
