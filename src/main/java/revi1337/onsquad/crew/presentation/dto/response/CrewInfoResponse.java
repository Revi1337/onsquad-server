package revi1337.onsquad.crew.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewInfoResponse(
        Long id,
        String name,
        String introduce,
        String detail,
        String imageUrl,
        String kakaoLink,
        List<Object> hashTags,
        SimpleMemberInfoResponse crewOwner
) {
    public static CrewInfoResponse from(CrewInfoDto crewInfoDto) {
        return new CrewInfoResponse(
                crewInfoDto.id(),
                crewInfoDto.name(),
                crewInfoDto.introduce(),
                crewInfoDto.detail(),
                crewInfoDto.imageUrl(),
                crewInfoDto.kakaoLink(),
                crewInfoDto.hashTags(),
                SimpleMemberInfoResponse.from(crewInfoDto.crewOwner())
        );
    }
}
