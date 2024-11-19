package revi1337.onsquad.crew_member.presentation.dto.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;

public record CrewMemberResponse(
        SimpleMemberInfoResponse memberInfo,
        LocalDateTime participantAt
) {
    public static CrewMemberResponse from(CrewMemberDto crewMemberDto) {
        return new CrewMemberResponse(
                SimpleMemberInfoResponse.from(crewMemberDto.memberInfo()),
                crewMemberDto.participantAt()
        );
    }
}
