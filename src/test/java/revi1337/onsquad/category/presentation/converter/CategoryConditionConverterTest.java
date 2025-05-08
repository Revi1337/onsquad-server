package revi1337.onsquad.category.presentation.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;

class CategoryConditionConverterTest {

    private final CategoryConditionConverter converter = new CategoryConditionConverter();

    @Test
    @DisplayName("존재하는 CategoryType Value 를 CategoryType 으로 Convert 에 성공한다.")
    void success1() {
        String categoryText = "게임";

        CategoryCondition result = converter.convert(categoryText);

        assertThat(result).isNotNull();
        assertThat(result.categoryType()).isSameAs(CategoryType.GAME);
    }

    @Test
    @DisplayName("존재하지 않는 CategoryType Value 를 CategoryType 으로 Convert 하면 ALL 을 반환한다.")
    void success2() {
        String invalidCategoryText = "INVALID_TEXT";

        CategoryCondition result = converter.convert(invalidCategoryText);

        assertThat(result).isNotNull();
        assertThat(result.categoryType()).isSameAs(CategoryType.ALL);
    }

    @Test
    @DisplayName("CategoryType Value 가 null 일 때, CategoryType 으로 Convert 하면 ALL 을 반환한다.")
    void success3() {
        String nullCategoryText = null;

        CategoryCondition result = converter.convert(nullCategoryText);

        assertThat(result).isNotNull();
        assertThat(result.categoryType()).isSameAs(CategoryType.ALL);
    }
}
