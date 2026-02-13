package revi1337.onsquad.crew_member.domain.model;

import java.time.LocalDateTime;

public record CrewRankerCandidate(
        Long crewId,
        int rank,
        long score,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime lastActivityTime
) {

    public static CrewRankerCandidate from(Long crewId, Long memberId, int rank, long score, LocalDateTime lastActivityTime) {
        return new CrewRankerCandidate(crewId, rank, score, memberId, null, null, lastActivityTime);
    }

    public CrewRankerCandidate withRankAndProfile(int rank, RankerProfile profile) {
        return new CrewRankerCandidate(
                crewId,
                rank,
                score,
                memberId,
                profile.getNickname(),
                profile.getMbtiOrDefault(),
                lastActivityTime
        );
    }
}
