package revi1337.onsquad.category.application.response;

import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;

public record CategoryResponse(
        CategoryType categoryType
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getCategoryType());
    }
}
