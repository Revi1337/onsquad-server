package revi1337.onsquad.crew_request.presentation.dto.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_request.application.dto.CrewRequestDto;

public record CrewRequestResponse(
        Long id,
        LocalDateTime requestAt
) {

    public static CrewRequestResponse from(CrewRequestDto crewRequestDto) {
        return new CrewRequestResponse(
                crewRequestDto.id(),
                crewRequestDto.requestAt()
        );
    }
}
