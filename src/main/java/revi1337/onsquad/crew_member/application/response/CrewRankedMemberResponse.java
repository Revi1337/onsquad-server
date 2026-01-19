package revi1337.onsquad.crew_member.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;

public record CrewRankedMemberResponse(
        Long crewId,
        Long memberId,
        String nickname,
        String mbti,
        int rank,
        long score,
        LocalDateTime lastActivityTime
) {

    public static CrewRankedMemberResponse from(CrewRankedMember crewRankedMember) {
        return new CrewRankedMemberResponse(
                crewRankedMember.getCrewId(),
                crewRankedMember.getMemberId(),
                crewRankedMember.getNickname(),
                crewRankedMember.getMbti() != null ? crewRankedMember.getMbti() : "",
                crewRankedMember.getRank(),
                crewRankedMember.getScore(),
                crewRankedMember.getLastActivityTime()
        );
    }
}
