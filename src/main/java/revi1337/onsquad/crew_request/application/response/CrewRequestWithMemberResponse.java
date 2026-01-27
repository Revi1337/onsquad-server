package revi1337.onsquad.crew_request.application.response;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record CrewRequestWithMemberResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResponse requester
) {

    public static CrewRequestWithMemberResponse from(CrewRequest request) {
        return new CrewRequestWithMemberResponse(
                request.getId(),
                request.getRequestAt(),
                SimpleMemberResponse.from(request.getMember())
        );
    }
}
