package revi1337.onsquad.category.application.dto;

import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.vo.CategoryType;

public record CategoryDto(
        CategoryType categoryType
) {
    public static CategoryDto from(Category category) {
        return new CategoryDto(category.getCategoryType());
    }
}
