package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;

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
        List<String> categories,
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
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberInfoDto.from(simpleSquadInfoDomainDto.squadOwner())
        );
    }
}
