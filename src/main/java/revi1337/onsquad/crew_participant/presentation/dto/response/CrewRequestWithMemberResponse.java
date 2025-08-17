package revi1337.onsquad.crew_participant.presentation.dto.response;

import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;

public record CrewRequestWithMemberResponse(
        CrewRequestResponse request,
        SimpleMemberResponse member
) {
    public static CrewRequestWithMemberResponse from(CrewRequestWithMemberDto dto) {
        return new CrewRequestWithMemberResponse(
                CrewRequestResponse.from(dto.request()),
                SimpleMemberResponse.from(dto.member())
        );
    }
}
