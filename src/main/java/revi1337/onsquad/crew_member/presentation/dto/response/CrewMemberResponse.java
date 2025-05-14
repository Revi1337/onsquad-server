package revi1337.onsquad.crew_member.presentation.dto.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;

public record CrewMemberResponse(
        LocalDateTime participantAt,
        SimpleMemberInfoResponse member
) {
    public static CrewMemberResponse from(CrewMemberDto crewMemberDto) {
        return new CrewMemberResponse(
                crewMemberDto.participantAt(),
                SimpleMemberInfoResponse.from(crewMemberDto.member())
        );
    }
}
