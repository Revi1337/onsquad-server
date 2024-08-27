package revi1337.onsquad.crew.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import revi1337.onsquad.crew.dto.CrewAcceptDto;

public record CrewAcceptRequest (
        @NotEmpty String requestCrewName,
        @NotNull Long requestMemberId
) {
    public CrewAcceptDto toDto() {
        return new CrewAcceptDto(requestCrewName, requestMemberId);
    }
}
