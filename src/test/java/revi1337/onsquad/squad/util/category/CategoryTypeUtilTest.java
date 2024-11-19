package revi1337.onsquad.squad.util.category;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.category.util.CategoryTypeUtil;
import revi1337.onsquad.squad.error.exception.SquadDomainException;

@DisplayName("CategoryTypeUtil 테스트")
class CategoryTypeUtilTest {

    @Test
    @DisplayName("CategoryType 리스트 안에 ALL 이 포함되면 전체로 간주한다.")
    public void categoryUtilTest1() {
        // given
        List<String> stringCategories = List.of("전체", "등산");
        List<CategoryType> preCategoryTypes = CategoryType.fromTexts(stringCategories);

        // when
        List<CategoryType> categoryTypes = CategoryTypeUtil.extractPossible(preCategoryTypes);

        // then
        assertSoftly(softly -> {
            softly.assertThat(categoryTypes).hasSize(1);
            softly.assertThat(categoryTypes).element(0).isEqualTo(CategoryType.ALL);
        });
    }

    @Test
    @DisplayName("CategoryType 리스트 안에 null 이 포함되면 예외를 던진다.")
    public void categoryUtilTest2() {
        // given
        String dummyCategoryText = "dummy_category";
        List<String> stringCategories = List.of(dummyCategoryText, "등산");
        List<CategoryType> preCategoryTypes = CategoryType.fromTexts(stringCategories);

        // when & then
        assertThatThrownBy(() -> CategoryTypeUtil.extractPossible(preCategoryTypes))
                .isExactlyInstanceOf(SquadDomainException.InvalidCategory.class)
                .hasMessage("유효하지 않은 카테고리가 존재합니다.");
    }

    @Test
    @DisplayName("CategoryType 리스트에 ALL 이 들어있으면, null 이 들어와도 성공한다.")
    public void categoryUtilTest3() {
        // given
        String dummyCategoryText = "dummy_category";
        List<String> stringCategories = List.of("전체", dummyCategoryText);
        List<CategoryType> preCategoryTypes = CategoryType.fromTexts(stringCategories);

        // when
        List<CategoryType> categoryTypes = CategoryTypeUtil.extractPossible(preCategoryTypes);

        // then
        assertSoftly(softly -> {
            softly.assertThat(categoryTypes).hasSize(1);
            softly.assertThat(categoryTypes).element(0).isEqualTo(CategoryType.ALL);
        });
    }
}