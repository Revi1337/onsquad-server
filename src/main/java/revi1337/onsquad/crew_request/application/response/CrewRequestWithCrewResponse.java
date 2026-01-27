package revi1337.onsquad.crew_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.application.dto.response.SimpleCrewResponse;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

public record CrewRequestWithCrewResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleCrewResponse crew
) {

    public static CrewRequestWithCrewResponse from(CrewRequest request) {
        return new CrewRequestWithCrewResponse(
                request.getId(),
                request.getRequestAt(),
                SimpleCrewResponse.from(request.getCrew())
        );
    }
}
