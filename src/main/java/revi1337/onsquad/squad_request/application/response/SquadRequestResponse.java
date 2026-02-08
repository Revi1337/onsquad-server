package revi1337.onsquad.squad_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_request.domain.model.SquadRequestDetail;

public record SquadRequestResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResponse member
) {

    public static SquadRequestResponse from(SquadRequestDetail squadRequestDetail) {
        return new SquadRequestResponse(
                squadRequestDetail.id(),
                squadRequestDetail.requestAt(),
                SimpleMemberResponse.from(squadRequestDetail.member())
        );
    }
}
