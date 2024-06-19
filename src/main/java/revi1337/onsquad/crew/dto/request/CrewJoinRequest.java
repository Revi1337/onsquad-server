package revi1337.onsquad.crew.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.crew.dto.CrewJoinDto;

public record CrewJoinRequest(
        @NotEmpty String crewName
) {
    public CrewJoinDto toDto() {
        return new CrewJoinDto(crewName);
    }
}
