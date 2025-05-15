package revi1337.onsquad.crew_participant.application.dto;

import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithMemberDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record CrewRequestWithMemberDto(
        CrewRequestDto request,
        SimpleMemberInfoDto member
) {
    public static CrewRequestWithMemberDto from(CrewRequestWithMemberDomainDto crewRequestWithMemberDomainDto) {
        return new CrewRequestWithMemberDto(
                CrewRequestDto.from(crewRequestWithMemberDomainDto.request()),
                SimpleMemberInfoDto.from(crewRequestWithMemberDomainDto.memberInfo())
        );
    }
}
