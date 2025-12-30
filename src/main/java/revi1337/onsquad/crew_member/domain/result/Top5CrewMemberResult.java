package revi1337.onsquad.crew_member.domain.result;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewTopMember;

public record Top5CrewMemberResult(
        Long crewId,
        int rank,
        int score,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime lastActivityTime
) {

    public static Top5CrewMemberResult from(CrewTopMember crewTopMember) {
        return new Top5CrewMemberResult(
                crewTopMember.getCrewId(),
                crewTopMember.getRanks(),
                crewTopMember.getScore(),
                crewTopMember.getMemberId(),
                crewTopMember.getNickname(),
                crewTopMember.getMbti(),
                crewTopMember.getLastActivityTime()
        );
    }

    public CrewTopMember toEntity() {
        return new CrewTopMember(crewId, rank, score, memberId, nickname, mbti, lastActivityTime);
    }
}
