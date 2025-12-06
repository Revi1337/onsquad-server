package revi1337.onsquad.category.domain.entity.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryTypeTest {

    @Test
    @DisplayName("CategoryType 에 존재하는 text 가 있다면 Enum 을 반환한다.")
    void success1() {
        String categoryText = CategoryType.ACTIVITY.getText();

        CategoryType categoryType = CategoryType.fromText(categoryText);

        assertThat(categoryType).isSameAs(CategoryType.ACTIVITY);
    }

    @Test
    @DisplayName("CategoryType 에 존재하는 text 가 없다면 null 을 반환한다.")
    void success2() {
        String categoryText = "INVALID_CATEGORY";

        CategoryType categoryType = CategoryType.fromText(categoryText);

        assertThat(categoryType).isNull();
    }

    @Test
    @DisplayName("CategoryType text 들을 중복처리가 적용된다.")
    void success3() {
        List<String> categoryTypes = List.of(CategoryType.ACTIVITY.getText(), CategoryType.ACTIVITY.getText());

        List<CategoryType> results = CategoryType.fromTexts(categoryTypes);

        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("CategoryType text 들을 중복처리는 null 을 제외하고 진행된다.")
    void success4() {
        String categoryText = "INVALID_CATEGORY";
        List<String> categoryTypes = List.of(CategoryType.ACTIVITY.getText(), categoryText);

        List<CategoryType> results = CategoryType.fromTexts(categoryTypes);

        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("CategoryType Stream 을 뽑아내는데 성공한다.")
    void success5() {
        CategoryType[] values = CategoryType.values();

        List<CategoryType> categoryTypes = CategoryType.unmodifiableList();

        assertThat(categoryTypes).hasSize(values.length);
    }

    @Test
    @DisplayName("CategoryType 값들을 뽑아내는데 성공한다.")
    void success6() {
        List<String> texts = CategoryType.unmodifiableList().stream()
                .map(CategoryType::getText)
                .toList();

        List<String> categoryTexts = CategoryType.texts();

        assertThat(categoryTexts).containsExactlyInAnyOrder(texts.toArray(String[]::new));
    }
}
