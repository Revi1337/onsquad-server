package revi1337.onsquad.crew.domain.result;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;

public record Top5CrewMemberResult(
        Long crewId,
        int rank,
        int contribute,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime participateAt
) {

    public static Top5CrewMemberResult from(CrewTopMember crewTopMember) {
        return new Top5CrewMemberResult(
                crewTopMember.getCrewId(),
                crewTopMember.getRanks(),
                crewTopMember.getContribute(),
                crewTopMember.getMemberId(),
                crewTopMember.getNickname(),
                crewTopMember.getMbti(),
                crewTopMember.getParticipateAt()
        );
    }

    public CrewTopMember toEntity() {
        return new CrewTopMember(crewId, rank, contribute, memberId, nickname, mbti, participateAt);
    }
}
