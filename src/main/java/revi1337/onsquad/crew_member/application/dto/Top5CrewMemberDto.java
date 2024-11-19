package revi1337.onsquad.crew_member.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;

public record Top5CrewMemberDto(
        Long crewId,
        int rank,
        int counter,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime participateAt
) {
    public static Top5CrewMemberDto from(Top5CrewMemberDomainDto top5CrewMemberDomainDto) {
        return new Top5CrewMemberDto(
                top5CrewMemberDomainDto.crewId(),
                top5CrewMemberDomainDto.rank(),
                top5CrewMemberDomainDto.counter(),
                top5CrewMemberDomainDto.memberId(),
                top5CrewMemberDomainDto.nickname(),
                top5CrewMemberDomainDto.mbti() != null ? top5CrewMemberDomainDto.mbti() : "",
                top5CrewMemberDomainDto.participateAt()
        );
    }
}
