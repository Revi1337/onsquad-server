package revi1337.onsquad.squad_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public record SquadRequestResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResponse requester
) {

    public static SquadRequestResponse from(SquadRequest request) {
        return new SquadRequestResponse(
                request.getId(),
                request.getRequestAt(),
                SimpleMemberResponse.from(request.getMember())
        );
    }
}
