package revi1337.onsquad.crew_request.application.dto;

import revi1337.onsquad.crew_request.domain.dto.CrewRequestWithMemberDomainDto;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record CrewRequestWithMemberDto(
        CrewRequestDto request,
        SimpleMemberDto member
) {

    public static CrewRequestWithMemberDto from(CrewRequestWithMemberDomainDto crewRequestWithMemberDomainDto) {
        return new CrewRequestWithMemberDto(
                CrewRequestDto.from(crewRequestWithMemberDomainDto.request()),
                SimpleMemberDto.from(crewRequestWithMemberDomainDto.memberInfo())
        );
    }
}
