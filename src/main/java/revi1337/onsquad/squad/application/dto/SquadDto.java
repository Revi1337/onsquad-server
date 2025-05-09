package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;

public record SquadDto(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberInfoDto owner
) {
    public static SquadDto from(SquadDomainDto domainDto) {
        return new SquadDto(
                domainDto.id(),
                domainDto.title().getValue(),
                domainDto.capacity().getValue(),
                domainDto.capacity().getRemain(),
                domainDto.categories().stream()
                        .map(CategoryType::getText)
                        .toList(),
                SimpleMemberInfoDto.from(domainDto.owner())
        );
    }
}
