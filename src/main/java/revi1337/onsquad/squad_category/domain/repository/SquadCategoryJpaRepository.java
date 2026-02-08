package revi1337.onsquad.squad_category.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;
import revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory;

public interface SquadCategoryJpaRepository extends JpaRepository<SquadCategory, Long> {

    @Query("select new revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory(sc.squad.id, sc.category.categoryType)"
            + " from SquadCategory sc inner join sc.category where sc.squad.id in :squadIds")
    List<SimpleSquadCategory> fetchCategoriesBySquadIdIn(List<Long> squadIds);

    @Modifying
    @Query("delete from SquadCategory sc where sc.squad.id in :squadIds")
    int deleteBySquadIdIn(List<Long> squadIds);

}
