package revi1337.onsquad.squad.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;

public record SquadAcceptRequest(
        @NotEmpty String requestCrewName,
        @Positive Long squadId,
        @Positive Long requestMemberId
) {
    public SquadAcceptDto toDto() {
        return new SquadAcceptDto(requestCrewName, squadId, requestMemberId);
    }
}
