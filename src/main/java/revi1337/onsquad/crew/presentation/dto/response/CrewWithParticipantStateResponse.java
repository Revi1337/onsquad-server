package revi1337.onsquad.crew.presentation.dto.response;

import revi1337.onsquad.crew.domain.dto.CrewWithParticipantStateDto;

public record CrewWithParticipantStateResponse(
        boolean alreadyParticipant,
        CrewResponse crew
) {
    public static CrewWithParticipantStateResponse from(CrewWithParticipantStateDto stateDto) {
        return new CrewWithParticipantStateResponse(
                stateDto.alreadyParticipant(),
                CrewResponse.from(stateDto.crew())
        );
    }
}
