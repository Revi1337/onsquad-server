package revi1337.onsquad.crew_member.application.response;

import revi1337.onsquad.crew_member.domain.result.JoinedCrewResult;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record JoinedCrewResponse(
        Long id,
        String name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberDto owner
) {

    public static JoinedCrewResponse from(JoinedCrewResult joinedCrewResult) {
        return new JoinedCrewResponse(
                joinedCrewResult.id(),
                joinedCrewResult.name().getValue(),
                joinedCrewResult.imageUrl() != null ? joinedCrewResult.imageUrl() : "",
                joinedCrewResult.isOwner(),
                SimpleMemberDto.from(joinedCrewResult.owner())
        );
    }
}
