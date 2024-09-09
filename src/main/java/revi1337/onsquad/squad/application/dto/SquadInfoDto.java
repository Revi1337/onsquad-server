package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.member.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
import revi1337.onsquad.squad_category.application.dto.SquadCategoryDto;

import java.util.List;

public record SquadInfoDto(
        Long id,
        String title,
        String content,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<SquadCategoryDto> categories,
        SimpleMemberInfoDto squadOwner
) {
    public static SquadInfoDto from(SquadInfoDomainDto squadInfoDomainDto) {
        return new SquadInfoDto(
                squadInfoDomainDto.id(),
                squadInfoDomainDto.title().getValue(),
                squadInfoDomainDto.content().getValue(),
                squadInfoDomainDto.capacity().getValue(),
                squadInfoDomainDto.capacity().getRemain(),
                squadInfoDomainDto.address().getValue(),
                squadInfoDomainDto.address().getDetail(),
                squadInfoDomainDto.kakaoLink(),
                squadInfoDomainDto.discordLink(),
                squadInfoDomainDto.categories().stream()
                        .map(SquadCategoryDto::from)
                        .toList(),
                SimpleMemberInfoDto.from(squadInfoDomainDto.squadOwner())
        );
    }
}
