package revi1337.onsquad.category.domain;

import revi1337.onsquad.category.domain.vo.CategoryType;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    void insertBulkCategories(List<Category> categories);

    void saveAll(List<Category> categories);

    List<Category> findAll();

    List<Category> findAllCategories();

    Optional<Category> findById(Long id);

    List<Category> findCategoriesInSecondCache(List<CategoryType> categoryTypes);

}
