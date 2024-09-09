package revi1337.onsquad.squad.domain.category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import revi1337.onsquad.squad.domain.vo.category.CategoryType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    private Category(CategoryType categoryType) {
        this.id = categoryType.getPk();
        this.categoryType = categoryType;
    }

    public static List<Category> fromCategoryTypes(List<CategoryType> categoryTypes) {
        return categoryTypes.stream()
                .map(Category::fromCategoryType)
                .collect(Collectors.toList());
    }

    public static Category fromCategoryType(CategoryType categoryType) {
        return new Category(categoryType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;
        return id != null && Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
