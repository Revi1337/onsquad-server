package revi1337.onsquad.crew.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.crew.application.dto.CrewJoinDto;

public record CrewJoinRequest(
        @NotNull @Positive Long crewId
) {
    public CrewJoinDto toDto() {
        return new CrewJoinDto(crewId);
    }
}
