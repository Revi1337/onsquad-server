package revi1337.onsquad.crew_member.domain.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.backup.crew.domain.CrewTopCache;

public record Top5CrewMemberDomainDto(
        Long crewId,
        int rank,
        int counter,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime participateAt
) {
    public static Top5CrewMemberDomainDto from(CrewTopCache crewTopCache) {
        return new Top5CrewMemberDomainDto(
                crewTopCache.getCrewId(),
                crewTopCache.getRanks(),
                crewTopCache.getCounter(),
                crewTopCache.getMemberId(),
                crewTopCache.getNickname(),
                crewTopCache.getMbti(),
                crewTopCache.getParticipateAt()
        );
    }
}
