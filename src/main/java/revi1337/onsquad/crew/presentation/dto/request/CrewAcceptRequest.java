package revi1337.onsquad.crew.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.crew.application.dto.CrewAcceptDto;

public record CrewAcceptRequest (
        @NotEmpty String crewName,
        @Positive Long memberId
) {
    public CrewAcceptDto toDto() {
        return new CrewAcceptDto(crewName, memberId);
    }
}
