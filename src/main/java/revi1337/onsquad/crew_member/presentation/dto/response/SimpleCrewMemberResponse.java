package revi1337.onsquad.crew_member.presentation.dto.response;

import revi1337.onsquad.crew_member.application.dto.SimpleCrewMemberDto;

public record SimpleCrewMemberResponse(
        Long id,
        String nickname,
        String role
) {
    public static SimpleCrewMemberResponse from(SimpleCrewMemberDto simpleCrewMemberDto) {
        return new SimpleCrewMemberResponse(
                simpleCrewMemberDto.id(),
                simpleCrewMemberDto.nickname(),
                simpleCrewMemberDto.role()
        );
    }
}
