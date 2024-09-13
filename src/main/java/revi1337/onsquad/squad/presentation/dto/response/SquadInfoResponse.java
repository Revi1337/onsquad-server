package revi1337.onsquad.squad.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadInfoResponse(
        Long id,
        String title,
        String content,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<String> categories,
        SimpleMemberInfoResponse squadOwner
) {
    public static SquadInfoResponse from(SquadInfoDto squadInfoDto) {
        return new SquadInfoResponse(
                squadInfoDto.id(),
                squadInfoDto.title(),
                squadInfoDto.content(),
                squadInfoDto.capacity(),
                squadInfoDto.remain(),
                squadInfoDto.address(),
                squadInfoDto.addressDetail(),
                squadInfoDto.kakaoLink(),
                squadInfoDto.discordLink(),
                squadInfoDto.categories(),
                SimpleMemberInfoResponse.from(squadInfoDto.squadOwner())
        );
    }
}
