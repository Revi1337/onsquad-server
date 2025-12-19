package revi1337.onsquad.squad_category.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;

@Deprecated
public record SquadCategoryResult(
        Long id,
        String category
) {

    @QueryProjection
    public SquadCategoryResult(Long id, CategoryType category) {
        this(id, category.getText());
    }
}
