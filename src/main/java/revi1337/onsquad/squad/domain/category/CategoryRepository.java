package revi1337.onsquad.squad.domain.category;

import revi1337.onsquad.squad.domain.vo.category.CategoryType;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    void insertBulkCategories(List<Category> categories);

    void saveAll(List<Category> categories);

    List<Category> findAll();

    Optional<Category> findById(Long id);

    List<Category> findCategoriesInSecondCache(List<CategoryType> categoryTypes);

}
