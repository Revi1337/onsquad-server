package revi1337.onsquad.crew.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.crew.application.dto.SimpleCrewInfoDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimpleCrewInfoResponse(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberInfoResponse owner
) {
    public static SimpleCrewInfoResponse from(SimpleCrewInfoDto simpleCrewInfoDto) {
        return new SimpleCrewInfoResponse(
                simpleCrewInfoDto.id(),
                simpleCrewInfoDto.name(),
                simpleCrewInfoDto.introduce(),
                simpleCrewInfoDto.kakaoLink(),
                simpleCrewInfoDto.imageUrl(),
                SimpleMemberInfoResponse.from(simpleCrewInfoDto.owner())
        );
    }
}
