package revi1337.onsquad.squad_category.domain.repository;

import java.util.List;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;

public interface SquadCategoryRepository {

    int insertBatch(Long squadId, List<Category> categories);

    List<SquadCategory> fetchCategoriesBySquadIdIn(List<Long> squadIds);

    int deleteBySquadIdIn(List<Long> squadIds);

}
