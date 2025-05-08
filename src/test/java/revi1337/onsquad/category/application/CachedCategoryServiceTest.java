package revi1337.onsquad.category.application;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.common.ApplicationLayerTestSupport;

@Sql({"/h2-category.sql"})
class CachedCategoryServiceTest extends ApplicationLayerTestSupport {

    @SpyBean
    private DefaultCategoryService defaultCategoryService;

    @Autowired
    private CachedCategoryService cachedCategoryService;

    private List<String> cached;

    @BeforeEach
    void setUp() {
        cached = (List<String>) ReflectionTestUtils
                .getField(cachedCategoryService, "cachedCategories");
        cached.clear();
        reset(defaultCategoryService);
    }

    @Test
    @DisplayName("한번 캐싱된 Category 는 다신 조회되지 않는다.")
    void success() {
        cachedCategoryService.findCategories();
        cached.clear();
        reset(defaultCategoryService);

        cachedCategoryService.findCategories();

        verify(defaultCategoryService, times(1)).findCategories();
    }

    @Test
    @DisplayName("한번도 캐싱되지 않은 Category 는 조회된다.")
    void success1() {
        cachedCategoryService.findCategories();

        verify(defaultCategoryService, times(1)).findCategories();
    }
}
