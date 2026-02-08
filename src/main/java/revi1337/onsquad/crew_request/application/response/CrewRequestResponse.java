package revi1337.onsquad.crew_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record CrewRequestResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResponse requester
) {

    public static CrewRequestResponse from(CrewRequest request) {
        return new CrewRequestResponse(
                request.getId(),
                request.getRequestAt(),
                SimpleMemberResponse.from(request.getMember())
        );
    }
}
