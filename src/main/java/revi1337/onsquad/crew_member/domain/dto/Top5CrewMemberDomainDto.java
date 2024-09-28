package revi1337.onsquad.crew_member.domain.dto;

import revi1337.onsquad.backup.crew.domain.CrewTopCache;

import java.time.LocalDateTime;

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
                crewTopCache.getRank(),
                crewTopCache.getCounter(),
                crewTopCache.getMemberId(),
                crewTopCache.getNickname(),
                crewTopCache.getMbti(),
                crewTopCache.getParticipateAt()
        );
    }
}
