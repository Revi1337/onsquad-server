package revi1337.onsquad.crew_member.domain.result;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;

public record CrewRankedMemberResult(
        Long crewId,
        int rank,
        double score,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime lastActivityTime
) {

    public static CrewRankedMemberResult from(CrewRankedMember crewRankedMember) {
        return new CrewRankedMemberResult(
                crewRankedMember.getCrewId(),
                crewRankedMember.getRank(),
                crewRankedMember.getScore(),
                crewRankedMember.getMemberId(),
                crewRankedMember.getNickname(),
                crewRankedMember.getMbti(),
                crewRankedMember.getLastActivityTime()
        );
    }

    public CrewRankedMember toEntity() {
        return new CrewRankedMember(crewId, rank, score, memberId, nickname, mbti, lastActivityTime);
    }
}
