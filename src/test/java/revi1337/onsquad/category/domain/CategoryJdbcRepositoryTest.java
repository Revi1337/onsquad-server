package revi1337.onsquad.category.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.PersistenceLayerTestSupport;

@Import(CategoryJdbcRepository.class)
class CategoryJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private CategoryJdbcRepository categoryJdbcRepository;

    @Test
    @DisplayName("Category Batch Insert 에 성공한다.")
    void success() {
        Category category1 = new Category(CategoryType.GAME);
        Category category2 = new Category(CategoryType.MOVIE);
        Category category3 = new Category(CategoryType.ALL);

        int influenced = categoryJdbcRepository.batchInsert(List.of(category1, category2, category3));

        assertThat(influenced).isEqualTo(3);
    }
}
