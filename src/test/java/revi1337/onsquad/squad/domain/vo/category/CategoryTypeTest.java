package revi1337.onsquad.squad.domain.vo.category;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.category.domain.vo.CategoryType;

@DisplayName("CategoryType 테스트")
class CategoryTypeTest {

    @Test
    @DisplayName("문자열을 통해 CategoryType 들을 생성한다.")
    public void categoryTest1() {
        // given
        List<String> stringCategories = List.of("전체", "등산");

        // when
        List<CategoryType> categoryTypes = CategoryType.fromTexts(stringCategories);

        // then
        assertSoftly(softly -> {
            softly.assertThat(categoryTypes).hasSize(2);
            softly.assertThat(categoryTypes).element(0).isEqualTo(CategoryType.ALL);
            softly.assertThat(categoryTypes).element(1).isEqualTo(CategoryType.HIKING);
        });
    }

    @Test
    @DisplayName("문자열을 통해 CategoryType 들을 생성한다. 2")
    public void categoryTest2() {
        // given
        String dummyCategoryText = "dummy_category";
        List<String> stringCategories = List.of("전체", "등산", dummyCategoryText);

        // when
        List<CategoryType> categoryTypes = CategoryType.fromTexts(stringCategories);

        // then
        assertSoftly(softly -> {
            softly.assertThat(categoryTypes).hasSize(3);
            softly.assertThat(categoryTypes).element(0).isEqualTo(CategoryType.ALL);
            softly.assertThat(categoryTypes).element(1).isEqualTo(CategoryType.HIKING);
            softly.assertThat(categoryTypes).element(2).isNull();
        });
    }
}