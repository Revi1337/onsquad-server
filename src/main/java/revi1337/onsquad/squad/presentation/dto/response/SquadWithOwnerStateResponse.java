package revi1337.onsquad.squad.presentation.dto.response;

import revi1337.onsquad.squad.application.dto.SquadWithOwnerStateDto;

public record SquadWithOwnerStateResponse(
        Boolean isOwner,
        SquadResponse squad
) {
    public static SquadWithOwnerStateResponse from(SquadWithOwnerStateDto dto) {
        return new SquadWithOwnerStateResponse(
                dto.isOwner(),
                SquadResponse.from(dto.squad())
        );
    }
}
