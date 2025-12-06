package revi1337.onsquad.category.application.dto;

import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;

public record CategoryDto(
        CategoryType categoryType
) {

    public static CategoryDto from(Category category) {
        return new CategoryDto(category.getCategoryType());
    }
}
