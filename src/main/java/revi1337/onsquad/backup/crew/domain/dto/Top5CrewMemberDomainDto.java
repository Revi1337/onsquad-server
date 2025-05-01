package revi1337.onsquad.backup.crew.domain.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.backup.crew.domain.CrewTopMember;

public record Top5CrewMemberDomainDto(
        Long crewId,
        int rank,
        int counter,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime participateAt
) {
    public static Top5CrewMemberDomainDto from(CrewTopMember crewTopMember) {
        return new Top5CrewMemberDomainDto(
                crewTopMember.getCrewId(),
                crewTopMember.getRanks(),
                crewTopMember.getCounter(),
                crewTopMember.getMemberId(),
                crewTopMember.getNickname(),
                crewTopMember.getMbti(),
                crewTopMember.getParticipateAt()
        );
    }
}
