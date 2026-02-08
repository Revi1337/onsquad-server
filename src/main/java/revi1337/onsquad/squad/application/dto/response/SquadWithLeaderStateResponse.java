package revi1337.onsquad.squad.application.dto.response;

import revi1337.onsquad.squad.domain.model.SquadWithLeaderState;

public record SquadWithLeaderStateResponse(
        boolean isLeader,
        SimpleSquadResponse squad
) {

    public static SquadWithLeaderStateResponse from(SquadWithLeaderState domainDto) {
        return new SquadWithLeaderStateResponse(
                domainDto.isLeader(),
                SimpleSquadResponse.from(domainDto.squad())
        );
    }
}
