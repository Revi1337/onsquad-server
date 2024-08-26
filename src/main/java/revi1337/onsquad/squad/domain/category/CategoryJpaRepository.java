package revi1337.onsquad.squad.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.squad.domain.vo.category.CategoryType;

import java.util.List;
import java.util.stream.Collectors;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    default List<Category> findCategoriesInSecondCache(List<CategoryType> categoryTypes) {
        return categoryTypes.stream()
                .map(categoryType -> findById(categoryType.getPk()).get())
                .collect(Collectors.toList());
    }
}
