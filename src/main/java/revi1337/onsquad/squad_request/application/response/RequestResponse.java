package revi1337.onsquad.squad_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public record RequestResponse(
        Long id,
        LocalDateTime requestAt
) {

    public static RequestResponse from(SquadRequest squadRequest) {
        return new RequestResponse(
                squadRequest.getId(),
                squadRequest.getRequestAt()
        );
    }
}
