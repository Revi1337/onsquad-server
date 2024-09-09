package revi1337.onsquad.squad.dto.category;

import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.vo.CategoryType;

public record CategoryDto(
        CategoryType categoryType
) {
    public static CategoryDto from(Category category) {
        return new CategoryDto(category.getCategoryType());
    }
}
