package revi1337.onsquad.squad.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;

public record SquadAcceptRequest(
        @NotNull @Positive Long crewId,
        @NotNull @Positive Long squadId,
        @NotNull @Positive Long memberId
) {
    public SquadAcceptDto toDto() {
        return new SquadAcceptDto(crewId, squadId, memberId);
    }
}
