package revi1337.onsquad.category.presentation.dto.response;

import revi1337.onsquad.category.application.dto.CategoryDto;

public record CategoryResponse(
        String name
) {
    public static CategoryResponse from(CategoryDto category) {
        return new CategoryResponse(category.categoryType().getText());
    }
}
