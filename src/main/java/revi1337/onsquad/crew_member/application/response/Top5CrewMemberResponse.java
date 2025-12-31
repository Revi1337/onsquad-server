package revi1337.onsquad.crew_member.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewTopMember;

public record Top5CrewMemberResponse(
        Long crewId,
        int rank,
        double score,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime lastActivityTime
) {

    public static Top5CrewMemberResponse from(CrewTopMember crewTopMember) {
        return new Top5CrewMemberResponse(
                crewTopMember.getCrewId(),
                crewTopMember.getRanks(),
                crewTopMember.getScore(),
                crewTopMember.getMemberId(),
                crewTopMember.getNickname(),
                crewTopMember.getMbti() != null ? crewTopMember.getMbti() : "",
                crewTopMember.getLastActivityTime()
        );
    }
}
