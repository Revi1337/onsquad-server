package revi1337.onsquad.category.domain;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.category.domain.vo.CategoryType;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    default List<Category> findAllCategories() {
        return CategoryType.unmodifiableList().stream()
                .map(categoryType -> findById(categoryType.getPk()).get())
                .collect(Collectors.toList());
    }

    default List<Category> findCategoriesInSecondCache(List<CategoryType> categoryTypes) {
        return categoryTypes.stream()
                .map(categoryType -> findById(categoryType.getPk()).get())
                .collect(Collectors.toList());
    }
}
