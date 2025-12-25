package revi1337.onsquad.category.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad.error.SquadDomainException;

class CategoryTypeUtilTest {

    @Test
    @DisplayName("CategoryType 이 null 이면 검증에 실패한다.")
    void fail() {
        CategoryType categoryType = null;

        assertThatThrownBy(() -> CategoryTypeUtil.validateCategory(categoryType))
                .isExactlyInstanceOf(SquadDomainException.InvalidCategory.class);
    }

    @Test
    @DisplayName("CategoryType 에 '전체' 가 들어오면 전체 카테고리 하나만 반환한다.")
    void success() {
        List<CategoryType> categoryTypes = List.of(CategoryType.ALL, CategoryType.MOVIE);

        List<CategoryType> possible = CategoryTypeUtil.extractPossible(categoryTypes);

        assertThat(possible).hasSize(1);
        assertThat(possible.get(0)).isSameAs(CategoryType.ALL);
    }
}
