package revi1337.onsquad.crew.domain.result;

import revi1337.onsquad.crew.application.dto.response.CrewResponse;

public record CrewWithParticipantResult(
        boolean alreadyParticipant,
        CrewResponse crew
) {

    public static CrewWithParticipantResult from(boolean alreadyParticipant, CrewResult domainDto) {
        return new CrewWithParticipantResult(
                alreadyParticipant,
                CrewResponse.from(domainDto)
        );
    }
}
