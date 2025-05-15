package revi1337.onsquad.crew_participant.presentation.dto.response;

import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;

public record CrewRequestWithMemberResponse(
        CrewRequestResponse request,
        SimpleMemberInfoResponse member
) {
    public static CrewRequestWithMemberResponse from(CrewRequestWithMemberDto dto) {
        return new CrewRequestWithMemberResponse(
                CrewRequestResponse.from(dto.request()),
                SimpleMemberInfoResponse.from(dto.member())
        );
    }
}
