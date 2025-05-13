package revi1337.onsquad.squad.presentation.dto.response;

import revi1337.onsquad.squad.application.dto.SquadWithParticipantStateDto;

public record SquadWithParticipantStateResponse(
        boolean alreadyParticipant,
        SquadInfoResponse squad
) {
    public static SquadWithParticipantStateResponse from(SquadWithParticipantStateDto squadWithParticipantStateDto) {
        return new SquadWithParticipantStateResponse(
                squadWithParticipantStateDto.alreadyParticipant(),
                SquadInfoResponse.from(squadWithParticipantStateDto.squad())
        );
    }
}
