package revi1337.onsquad.crew.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.result.CrewResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewWithParticipantStateResponse(
        Boolean alreadyParticipant,
        CrewResponse crew
) {

    public static CrewWithParticipantStateResponse from(Boolean alreadyParticipant, CrewResult result) {
        return new CrewWithParticipantStateResponse(
                alreadyParticipant,
                CrewResponse.from(result)
        );
    }

    public static CrewWithParticipantStateResponse from(Boolean alreadyParticipant, Crew crew) {
        return new CrewWithParticipantStateResponse(
                alreadyParticipant,
                CrewResponse.from(crew)
        );
    }
}
