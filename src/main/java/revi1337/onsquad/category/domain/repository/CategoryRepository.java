package revi1337.onsquad.category.domain.repository;

import java.util.List;
import revi1337.onsquad.category.domain.entity.Category;

public interface CategoryRepository {

    List<Category> findAll();

}
