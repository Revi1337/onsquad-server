package revi1337.onsquad.crew_member.application.dto;

import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;

import java.time.LocalDateTime;

public record Top5CrewMemberDto(
        int rank,
        int counter,
        Long memberId,
        String nickname,
        LocalDateTime participateAt
) {
    public static Top5CrewMemberDto from(Top5CrewMemberDomainDto top5CrewMemberDomainDto) {
        return new Top5CrewMemberDto(
                top5CrewMemberDomainDto.rank(),
                top5CrewMemberDomainDto.counter(),
                top5CrewMemberDomainDto.memberId(),
                top5CrewMemberDomainDto.nickname().getValue(),
                top5CrewMemberDomainDto.participateAt()
        );
    }
}
