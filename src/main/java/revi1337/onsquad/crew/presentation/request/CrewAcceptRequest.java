package revi1337.onsquad.crew.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.crew.application.dto.CrewAcceptDto;

public record CrewAcceptRequest(
        @NotNull @Positive Long memberId
) {

    public CrewAcceptDto toDto() {
        return new CrewAcceptDto(memberId);
    }
}
