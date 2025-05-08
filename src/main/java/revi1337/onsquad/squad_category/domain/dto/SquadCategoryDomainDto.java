package revi1337.onsquad.squad_category.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.category.domain.vo.CategoryType;

@Deprecated
public record SquadCategoryDomainDto(
        Long id,
        String category
) {
    @QueryProjection
    public SquadCategoryDomainDto(Long id, CategoryType category) {
        this(id, category.getText());
    }
}
