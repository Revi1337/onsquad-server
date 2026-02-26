package revi1337.onsquad.squad_category.domain.repository;

import java.util.List;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;
import revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory;

public interface SquadCategoryRepository {

    int insertBatch(List<SquadCategory> squadCategories);

    List<SimpleSquadCategory> fetchCategoriesBySquadIdIn(List<Long> squadIds);

    int deleteBySquadIdIn(List<Long> squadIds);

}
