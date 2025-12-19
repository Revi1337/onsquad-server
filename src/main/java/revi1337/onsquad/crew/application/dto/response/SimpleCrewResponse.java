package revi1337.onsquad.crew.application.dto.response;

import revi1337.onsquad.crew.domain.result.SimpleCrewResult;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

public record SimpleCrewResponse(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberDto owner
) {

    public static SimpleCrewResponse from(SimpleCrewResult simpleCrewResult) {
        return new SimpleCrewResponse(
                simpleCrewResult.id(),
                simpleCrewResult.name().getValue(),
                simpleCrewResult.introduce() != null ? simpleCrewResult.introduce().getValue() : "",
                simpleCrewResult.kakaoLink() != null ? simpleCrewResult.kakaoLink() : "",
                simpleCrewResult.imageUrl() != null ? simpleCrewResult.imageUrl() : "",
                SimpleMemberDto.from(simpleCrewResult.owner())
        );
    }
}
