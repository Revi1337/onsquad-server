package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadDomainDto;

public record SimpleSquadDto(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberInfoDto leader
) {
    public static SimpleSquadDto from(SimpleSquadDomainDto simpleSquadDomainDto) {
        return new SimpleSquadDto(
                simpleSquadDomainDto.id(),
                simpleSquadDomainDto.title().getValue(),
                simpleSquadDomainDto.capacity().getValue(),
                simpleSquadDomainDto.capacity().getRemain(),
                simpleSquadDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberInfoDto.from(simpleSquadDomainDto.leader())
        );
    }
}
