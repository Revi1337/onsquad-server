package revi1337.onsquad.category.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    public Category(CategoryType categoryType) {
        this.id = categoryType.getPk();
        this.categoryType = categoryType;
    }

    public static List<Category> fromCategoryTypes(List<CategoryType> categoryTypes) {
        return categoryTypes.stream()
                .filter(type -> type != CategoryType.ALL)
                .distinct()
                .map(Category::new)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category category)) {
            return false;
        }
        return id != null && Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
