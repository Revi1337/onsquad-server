package revi1337.onsquad.squad_category.domain.result;

import revi1337.onsquad.category.domain.entity.vo.CategoryType;

public record SquadCategoryResult(
        Long squadId,
        CategoryType categoryType
) {

}
