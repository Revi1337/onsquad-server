package revi1337.onsquad.squad.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.squad.application.dto.SquadJoinDto;

public record SquadJoinRequest(
        @NotEmpty String crewName,
        @Positive Long squadId
) {
    public SquadJoinDto toDto() {
        return new SquadJoinDto(crewName, squadId);
    }
}
