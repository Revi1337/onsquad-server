package revi1337.onsquad.category.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryImplTest {

    @Mock
    private CategoryJdbcRepository categoryJdbcRepository;

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @InjectMocks
    private CategoryRepositoryImpl categoryRepository;

    @Test
    @DisplayName("batchInsert 위임에 성공한다.")
    void success1() {
        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);
        List<Category> mockList = List.of(category1, category2);

        categoryRepository.batchInsert(mockList);

        verify(categoryJdbcRepository).batchInsert(mockList);
    }

    @Test
    @DisplayName("saveAll 위임에 성공한다.")
    void success2() {
        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);
        List<Category> mockList = List.of(category1, category2);

        categoryRepository.saveAll(mockList);

        verify(categoryJpaRepository).saveAll(mockList);
    }

    @Test
    @DisplayName("findAll 위임에 성공한다.")
    void success3() {
        categoryRepository.findAll();

        verify(categoryJpaRepository).findAll();
    }

    @Test
    @DisplayName("findById 위임에 성공한다.")
    void success4() {
        Long categoryId = 1L;

        categoryRepository.findById(categoryId);

        verify(categoryJpaRepository).findById(categoryId);
    }
}
