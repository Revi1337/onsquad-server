package revi1337.onsquad.squad.presentation.dto.response;

import revi1337.onsquad.squad.application.dto.SquadWithLeaderStateDto;

public record SquadWithLeaderStateResponse(
        boolean isLeader,
        SimpleSquadResponse squad
) {
    public static SquadWithLeaderStateResponse from(SquadWithLeaderStateDto dto) {
        return new SquadWithLeaderStateResponse(
                dto.isLeader(),
                SimpleSquadResponse.from(dto.squad())
        );
    }
}
