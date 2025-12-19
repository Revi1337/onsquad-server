package revi1337.onsquad.crew_request.application.response;

import revi1337.onsquad.crew.application.dto.response.SimpleCrewResponse;
import revi1337.onsquad.crew_request.domain.result.CrewRequestWithCrewResult;

public record CrewRequestWithCrewResponse(
        CrewRequestResponse request,
        SimpleCrewResponse crew
) {

    public static CrewRequestWithCrewResponse from(CrewRequestWithCrewResult crewRequestWithCrewResult) {
        return new CrewRequestWithCrewResponse(
                CrewRequestResponse.from(crewRequestWithCrewResult.request()),
                SimpleCrewResponse.from(crewRequestWithCrewResult.crew())
        );
    }
}
