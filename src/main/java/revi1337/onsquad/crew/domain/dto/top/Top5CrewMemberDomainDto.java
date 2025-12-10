package revi1337.onsquad.crew.domain.dto.top;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;

public record Top5CrewMemberDomainDto(
        Long crewId,
        int rank,
        int contribute,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime participateAt
) {

    public static Top5CrewMemberDomainDto from(CrewTopMember crewTopMember) {
        return new Top5CrewMemberDomainDto(
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
