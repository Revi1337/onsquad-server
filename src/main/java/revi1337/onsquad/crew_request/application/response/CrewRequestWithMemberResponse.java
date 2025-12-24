package revi1337.onsquad.crew_request.application.response;

import revi1337.onsquad.crew_request.domain.result.CrewRequestWithMemberResult;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record CrewRequestWithMemberResponse(
        CrewRequestResponse request,
        SimpleMemberResponse member
) {

    public static CrewRequestWithMemberResponse from(CrewRequestWithMemberResult crewRequestWithMemberResult) {
        return new CrewRequestWithMemberResponse(
                CrewRequestResponse.from(crewRequestWithMemberResult.request()),
                SimpleMemberResponse.from(crewRequestWithMemberResult.memberInfo())
        );
    }
}
