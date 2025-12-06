package revi1337.onsquad.category.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.category.domain.entity.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

}
