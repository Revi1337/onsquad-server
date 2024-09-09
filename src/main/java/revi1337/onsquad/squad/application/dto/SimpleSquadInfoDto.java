package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.member.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;
import revi1337.onsquad.squad_category.application.dto.SquadCategoryDto;

import java.util.List;

public record SimpleSquadInfoDto(
        Long id,
        String title,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        Boolean isOwner,
        List<SquadCategoryDto> categories,
        SimpleMemberInfoDto squadOwner
) {
    public static SimpleSquadInfoDto from(SimpleSquadInfoDomainDto simpleSquadInfoDomainDto) {
        return new SimpleSquadInfoDto(
                simpleSquadInfoDomainDto.id(),
                simpleSquadInfoDomainDto.title().getValue(),
                simpleSquadInfoDomainDto.capacity().getValue(),
                simpleSquadInfoDomainDto.capacity().getRemain(),
                simpleSquadInfoDomainDto.address().getValue(),
                simpleSquadInfoDomainDto.address().getDetail(),
                simpleSquadInfoDomainDto.kakaoLink(),
                simpleSquadInfoDomainDto.discordLink(),
                simpleSquadInfoDomainDto.isOwner(),
                simpleSquadInfoDomainDto.categories().stream()
                        .map(SquadCategoryDto::from)
                        .toList(),
                SimpleMemberInfoDto.from(simpleSquadInfoDomainDto.squadOwner())
        );
    }
}
