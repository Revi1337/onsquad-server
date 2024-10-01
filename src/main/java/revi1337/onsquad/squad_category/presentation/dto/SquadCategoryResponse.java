package revi1337.onsquad.squad_category.presentation.dto;

import revi1337.onsquad.squad_category.application.dto.SquadCategoryDto;

public record SquadCategoryResponse(
        Long id,
        String category
) {
    public static SquadCategoryResponse from(SquadCategoryDto squadCategoryDto) {
        return new SquadCategoryResponse(
                squadCategoryDto.id(),
                squadCategoryDto.category()
        );
    }
}
