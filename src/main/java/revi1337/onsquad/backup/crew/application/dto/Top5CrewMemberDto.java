package revi1337.onsquad.backup.crew.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.backup.crew.domain.CrewTopMember;

public record Top5CrewMemberDto(
        Long crewId,
        int rank,
        int contribute,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime participateAt
) {
    public static Top5CrewMemberDto from(CrewTopMember crewTopMember) {
        return new Top5CrewMemberDto(
                crewTopMember.getCrewId(),
                crewTopMember.getRanks(),
                crewTopMember.getContribute(),
                crewTopMember.getMemberId(),
                crewTopMember.getNickname(),
                crewTopMember.getMbti() != null ? crewTopMember.getMbti() : "",
                crewTopMember.getParticipateAt()
        );
    }
}
