package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadInfoDomainDto;

public record SimpleSquadInfoDto(
        Long id,
        String title,
        int capacity,
        int remain,
        Boolean isLeader,
        List<String> categories,
        SimpleMemberInfoDto leader
) {
    public static SimpleSquadInfoDto from(SimpleSquadInfoDomainDto simpleSquadInfoDomainDto) {
        return new SimpleSquadInfoDto(
                simpleSquadInfoDomainDto.id(),
                simpleSquadInfoDomainDto.title().getValue(),
                simpleSquadInfoDomainDto.capacity().getValue(),
                simpleSquadInfoDomainDto.capacity().getRemain(),
                simpleSquadInfoDomainDto.isLeader(),
                simpleSquadInfoDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberInfoDto.from(simpleSquadInfoDomainDto.squadOwner())
        );
    }
}
