package revi1337.onsquad.crew.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import revi1337.onsquad.crew.application.dto.CrewDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewResponse(
        Long id,
        String name,
        String introduce,
        String detail,
        String imageUrl,
        String kakaoLink,
        Long memberCount,
        List<String> hashtags,
        SimpleMemberResponse owner
) {
    public static CrewResponse from(CrewDto crewDto) {
        return new CrewResponse(
                crewDto.id(),
                crewDto.name(),
                crewDto.introduce(),
                crewDto.detail(),
                crewDto.imageUrl(),
                crewDto.kakaoLink(),
                crewDto.memberCnt(),
                crewDto.hashtagTypes(),
                SimpleMemberResponse.from(crewDto.owner())
        );
    }
}
