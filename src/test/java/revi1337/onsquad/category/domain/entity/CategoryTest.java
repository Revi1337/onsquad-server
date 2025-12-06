package revi1337.onsquad.category.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;

class CategoryTest {

    @Test
    @DisplayName("Category 생성에 성공한다.")
    void success() {
        CategoryType categoryType = CategoryType.ACTIVITY;

        Category category = new Category(categoryType);

        assertThat(category).isNotNull();
    }

    @Test
    @DisplayName("Category 들은 중복을 제거하여 생성된다.")
    void success2() {
        List<CategoryType> categoryTypes = List.of(CategoryType.ACTIVITY, CategoryType.ACTIVITY);

        List<Category> categories = Category.fromCategoryTypes(categoryTypes);

        assertThat(categories).hasSize(1);
    }

    @Test
    @DisplayName("Category 들에 ALL 이 들어있다면 ALL 을 제외한 리스트를 반환한다.")
    void success3() {
        List<CategoryType> categoryTypes = List.of(CategoryType.ACTIVITY, CategoryType.ALL);

        List<Category> categories = Category.fromCategoryTypes(categoryTypes);

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0)).isEqualTo(new Category(CategoryType.ACTIVITY));
    }

    @Test
    @DisplayName("Category 는 도메인 객체이기 때문에, id 가 null 이거나 값이 없으면 동등성 비교에 실패한다.")
    void success4() {
        Category category1 = new Category(CategoryType.ACTIVITY);
        Category category2 = new Category(CategoryType.HIKING);

        boolean equals = category1.equals(category2);

        assertThat(equals).isFalse();
    }
}
