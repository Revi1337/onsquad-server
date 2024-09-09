package revi1337.onsquad.crew_member.application.dto;

import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

import java.time.LocalDateTime;

public record CrewMemberDto(
        SimpleMemberInfoDto memberInfo,
        LocalDateTime participantAt
) {
    public static CrewMemberDto from(CrewMemberDomainDto crewMemberDomainDto) {
        return new CrewMemberDto(
                SimpleMemberInfoDto.from(crewMemberDomainDto.memberInfo()),
                crewMemberDomainDto.participantAt()
        );
    }
}
