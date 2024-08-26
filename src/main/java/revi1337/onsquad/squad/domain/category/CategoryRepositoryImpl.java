package revi1337.onsquad.squad.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad.domain.category.vo.CategoryType;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryJdbcRepository categoryJdbcRepository;

    @Override
    public void insertBulkCategories(List<Category> categories) {
        categoryJdbcRepository.insertBulkCategories(categories);
    }

    @Override
    public void saveAll(List<Category> categories) {
        categoryJpaRepository.saveAll(categories);
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id);
    }

    @Override
    public List<Category> findCategoriesInSecondCache(List<CategoryType> categoryTypes) {
        return categoryJpaRepository.findCategoriesInSecondCache(categoryTypes);
    }
}
