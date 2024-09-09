package revi1337.onsquad.crew_member.dto.response;

import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.time.LocalDateTime;

public record CrewMemberResponse(
        String nickname,
        SimpleMemberInfoResponse memberInfo,
        LocalDateTime participateAt
) {
    public static CrewMemberResponse from(CrewMemberDto crewMemberDto) {
        return new CrewMemberResponse(
                crewMemberDto.nickname(),
                SimpleMemberInfoResponse.from(crewMemberDto.memberInfo()),
                crewMemberDto.participateAt()
        );
    }
}
