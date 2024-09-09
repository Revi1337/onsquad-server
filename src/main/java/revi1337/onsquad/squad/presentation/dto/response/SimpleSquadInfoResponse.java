package revi1337.onsquad.squad.presentation.dto.response;

import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.application.dto.SimpleSquadInfoDto;
import revi1337.onsquad.squad_category.presentation.dto.SquadCategoryResponse;

import java.util.List;

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
        List<SquadCategoryResponse> categories,
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
                simpleSquadInfoDto.categories().stream()
                        .map(SquadCategoryResponse::from)
                        .toList(),
                SimpleMemberInfoResponse.from(simpleSquadInfoDto.squadOwner())
        );
    }
}
