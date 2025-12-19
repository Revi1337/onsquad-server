package revi1337.onsquad.crew_request.application.response;

import revi1337.onsquad.crew_request.domain.result.CrewRequestWithMemberResult;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record CrewRequestWithMemberResponse(
        CrewRequestResponse request,
        SimpleMemberDto member
) {

    public static CrewRequestWithMemberResponse from(CrewRequestWithMemberResult crewRequestWithMemberResult) {
        return new CrewRequestWithMemberResponse(
                CrewRequestResponse.from(crewRequestWithMemberResult.request()),
                SimpleMemberDto.from(crewRequestWithMemberResult.memberInfo())
        );
    }
}
