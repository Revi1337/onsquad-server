package revi1337.onsquad.squad.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;

public record SquadAcceptRequest(
        @NotNull @Positive Long memberId
) {
    public SquadAcceptDto toDto() {
        return new SquadAcceptDto(memberId);
    }
}
