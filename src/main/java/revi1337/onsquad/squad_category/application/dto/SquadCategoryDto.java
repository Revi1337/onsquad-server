package revi1337.onsquad.squad_category.application.dto;

import revi1337.onsquad.squad_category.domain.dto.SquadCategoryDomainDto;

@Deprecated
public record SquadCategoryDto(
        Long id,
        String category
) {
    public static SquadCategoryDto from(SquadCategoryDomainDto squadCategoryDomainDto) {
        return new SquadCategoryDto(
                squadCategoryDomainDto.id(),
                squadCategoryDomainDto.category()
        );
    }
}
