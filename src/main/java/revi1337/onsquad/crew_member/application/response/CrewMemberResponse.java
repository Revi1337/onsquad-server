package revi1337.onsquad.crew_member.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.result.CrewMemberResult;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record CrewMemberResponse(
        LocalDateTime participantAt,
        SimpleMemberResponse member
) {

    public static CrewMemberResponse from(CrewMemberResult crewMemberResult) {
        return new CrewMemberResponse(
                crewMemberResult.participantAt(),
                SimpleMemberResponse.from(crewMemberResult.memberInfo())
        );
    }
}
