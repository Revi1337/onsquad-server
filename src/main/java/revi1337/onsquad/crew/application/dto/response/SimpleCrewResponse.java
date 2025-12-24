package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.result.SimpleCrewResult;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

public record SimpleCrewResponse(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberResponse owner
) {

    public static SimpleCrewResponse from(SimpleCrewResult simpleCrewResult) {
        return new SimpleCrewResponse(
                simpleCrewResult.id(),
                simpleCrewResult.name(),
                simpleCrewResult.introduce() != null ? simpleCrewResult.introduce() : "",
                simpleCrewResult.kakaoLink() != null ? simpleCrewResult.kakaoLink() : "",
                simpleCrewResult.imageUrl() != null ? simpleCrewResult.imageUrl() : "",
                SimpleMemberResponse.from(simpleCrewResult.owner())
        );
    }
}
