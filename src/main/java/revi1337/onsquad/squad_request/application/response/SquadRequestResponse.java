package revi1337.onsquad.squad_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_request.domain.result.SquadRequestResult;

public record SquadRequestResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResponse member
) {

    public static SquadRequestResponse from(SquadRequestResult squadRequestResult) {
        return new SquadRequestResponse(
                squadRequestResult.id(),
                squadRequestResult.requestAt(),
                SimpleMemberResponse.from(squadRequestResult.member())
        );
    }
}
