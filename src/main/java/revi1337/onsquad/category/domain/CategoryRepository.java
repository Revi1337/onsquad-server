package revi1337.onsquad.category.domain;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    int batchInsert(List<Category> categories);

    void saveAll(List<Category> categories);

    List<Category> findAll();

    Optional<Category> findById(Long id);

}
