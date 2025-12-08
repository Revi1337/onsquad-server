package revi1337.onsquad.squad_request.presentation.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_request.application.dto.SquadRequestDto;

public record SquadRequestResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResponse member
) {

    public static SquadRequestResponse from(SquadRequestDto squadRequestDto) {
        return new SquadRequestResponse(
                squadRequestDto.id(),
                squadRequestDto.requestAt(),
                SimpleMemberResponse.from(squadRequestDto.member())
        );
    }
}
