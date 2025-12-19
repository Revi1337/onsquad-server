package revi1337.onsquad.crew_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_request.domain.result.CrewRequestResult;

public record CrewRequestResponse(
        Long id,
        LocalDateTime requestAt
) {

    public static CrewRequestResponse from(CrewRequestResult crewRequestResult) {
        return new CrewRequestResponse(
                crewRequestResult.id(),
                crewRequestResult.requestAt()
        );
    }
}
