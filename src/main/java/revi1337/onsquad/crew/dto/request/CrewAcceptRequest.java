package revi1337.onsquad.crew.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import revi1337.onsquad.crew.dto.CrewAcceptDto;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.presentation.deserializer.JoinStatusDeserializer;

public record CrewAcceptRequest (
        @NotEmpty String requestCrewName,
        @NotNull Long requestMemberId,
        @NotNull @JsonDeserialize(using = JoinStatusDeserializer.class) JoinStatus requestStatus
) {
    public CrewAcceptDto toDto() {
        return new CrewAcceptDto(requestCrewName, requestMemberId, requestStatus);
    }
}
