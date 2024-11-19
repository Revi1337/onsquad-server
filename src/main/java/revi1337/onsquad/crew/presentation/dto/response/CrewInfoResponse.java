package revi1337.onsquad.crew.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewInfoResponse(
        Long id,
        String name,
        String introduce,
        String detail,
        String imageUrl,
        String kakaoLink,
        List<Object> hashtags,
        SimpleMemberInfoResponse crewOwner
) {
    public static CrewInfoResponse from(CrewInfoDto crewInfoDto) {
        ArrayList<Object> hashTags = new ArrayList<>();
        hashTags.add(crewInfoDto.memberCnt());
        hashTags.addAll(crewInfoDto.hashtagTypes());

        return new CrewInfoResponse(
                crewInfoDto.id(),
                crewInfoDto.name(),
                crewInfoDto.introduce(),
                crewInfoDto.detail(),
                crewInfoDto.imageUrl(),
                crewInfoDto.kakaoLink(),
                hashTags,
                SimpleMemberInfoResponse.from(crewInfoDto.crewOwner())
        );
    }
}
