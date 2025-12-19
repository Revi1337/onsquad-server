package revi1337.onsquad.squad_category.application.response;

import revi1337.onsquad.squad_category.domain.result.SquadCategoryResult;

@Deprecated
public record SquadCategoryResponse(
        Long id,
        String category
) {

    public static SquadCategoryResponse from(SquadCategoryResult squadCategoryResult) {
        return new SquadCategoryResponse(
                squadCategoryResult.id(),
                squadCategoryResult.category()
        );
    }
}
