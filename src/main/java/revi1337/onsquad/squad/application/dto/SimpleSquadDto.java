package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad.domain.dto.SimpleSquadDomainDto;

public record SimpleSquadDto(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberDto leader
) {

    public static SimpleSquadDto from(SimpleSquadDomainDto simpleSquadDomainDto) {
        return new SimpleSquadDto(
                simpleSquadDomainDto.id(),
                simpleSquadDomainDto.title().getValue(),
                simpleSquadDomainDto.capacity(),
                simpleSquadDomainDto.capacity(),
                simpleSquadDomainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberDto.from(simpleSquadDomainDto.leader())
        );
    }
}
