package revi1337.onsquad.crew_member.application.response;

import revi1337.onsquad.crew_member.domain.result.SimpleCrewMemberResult;

public record SimpleCrewMemberResponse(
        Long id,
        String nickname,
        String role
) {

    public static SimpleCrewMemberResponse from(SimpleCrewMemberResult simpleCrewMemberResult) {
        return new SimpleCrewMemberResponse(
                simpleCrewMemberResult.id(),
                simpleCrewMemberResult.nickname().getValue(),
                simpleCrewMemberResult.role().getText()
        );
    }
}
