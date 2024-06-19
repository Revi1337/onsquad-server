package revi1337.onsquad.crew.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.crew.dto.CrewAcceptDto;

public record CrewAcceptRequest (
        @NotEmpty String crewName
) {
    public CrewAcceptDto toDto() {
        return new CrewAcceptDto(crewName);
    }
}
