package revi1337.onsquad.squad.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoDto;

public record SimpleSquadInfoResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        Boolean isOwner,
        List<String> categories,
        SimpleMemberInfoResponse squadOwner
) {
    public static SimpleSquadInfoResponse from(SimpleSquadInfoDto simpleSquadInfoDto) {
        return new SimpleSquadInfoResponse(
                simpleSquadInfoDto.id(),
                simpleSquadInfoDto.title(),
                simpleSquadInfoDto.capacity(),
                simpleSquadInfoDto.remain(),
                simpleSquadInfoDto.address(),
                simpleSquadInfoDto.addressDetail(),
                simpleSquadInfoDto.kakaoLink(),
                simpleSquadInfoDto.discordLink(),
                simpleSquadInfoDto.isOwner(),
                simpleSquadInfoDto.categories(),
                SimpleMemberInfoResponse.from(simpleSquadInfoDto.squadOwner())
        );
    }
}
