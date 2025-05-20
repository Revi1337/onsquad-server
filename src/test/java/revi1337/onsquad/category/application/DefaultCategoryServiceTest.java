package revi1337.onsquad.category.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.ApplicationLayerTestSupport;

class DefaultCategoryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private DefaultCategoryService defaultCategoryService;

    @Test
    @DisplayName("모든 Category 값들 조회에 성공한다.")
    void success() {
        List<String> categoryTexts = CategoryType.texts();

        List<String> categories = defaultCategoryService.findCategories();

        assertThat(categories).containsExactlyInAnyOrder(categoryTexts.toArray(String[]::new));
    }
}
