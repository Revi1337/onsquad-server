package revi1337.onsquad.crew_member.domain.model;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.member.domain.entity.Member;

public record CrewRankerDetail(
        Long crewId,
        int rank,
        long score,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime lastActivityTime
) {

    public static CrewRankerDetail from(CrewRanker crewRanker) {
        return new CrewRankerDetail(
                crewRanker.getCrewId(),
                crewRanker.getRank(),
                crewRanker.getScore(),
                crewRanker.getMemberId(),
                crewRanker.getNickname(),
                crewRanker.getMbti(),
                crewRanker.getLastActivityTime()
        );
    }

    public static CrewRankerDetail from(Long crewId, Long memberId, int rank, long score, LocalDateTime lastActivityTime) {
        return new CrewRankerDetail(crewId, rank, score, memberId, null, null, lastActivityTime);
    }

    public CrewRanker toEntity() {
        return new CrewRanker(crewId, rank, score, memberId, nickname, mbti, lastActivityTime);
    }

    public CrewRanker toEntity(Member member) {
        return new CrewRanker(
                crewId,
                rank,
                score,
                memberId,
                member.getNickname().getValue(),
                member.getMbti() == null ? "" : member.getMbti().name(),
                lastActivityTime
        );
    }

    public CrewRanker toEntity(int rank, Member member) {
        return new CrewRanker(
                crewId,
                rank,
                score,
                memberId,
                member.getNickname().getValue(),
                member.getMbti() == null ? "" : member.getMbti().name(),
                lastActivityTime
        );
    }
}
