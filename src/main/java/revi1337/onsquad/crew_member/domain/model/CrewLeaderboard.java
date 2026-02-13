package revi1337.onsquad.crew_member.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CrewLeaderboard {

    private final List<CrewRankerCandidate> candidates;

    public static CrewLeaderboard empty() {
        return new CrewLeaderboard(Collections.emptyList());
    }

    public CrewLeaderboard(List<CrewRankerCandidate> candidates) {
        this.candidates = Collections.unmodifiableList(candidates);
    }

    public int size() {
        return candidates.size();
    }

    public List<CrewRankerCandidate> selectRankers(int limit, Map<Long, RankerProfile> profileMap) {
        int currentRank = 1;
        List<CrewRankerCandidate> selectedRankers = new ArrayList<>();
        for (CrewRankerCandidate candidate : candidates) {
            RankerProfile profile = profileMap.get(candidate.memberId());
            if (profile != null && currentRank <= limit) {
                selectedRankers.add(candidate.withRankAndProfile(currentRank++, profile));
            }
        }
        return selectedRankers;
    }

    public Stream<CrewRankerCandidate> candidateStream() {
        return candidates.stream();
    }
}
