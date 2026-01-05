package revi1337.onsquad.squad_category.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;

public interface SquadCategoryJpaRepository extends JpaRepository<SquadCategory, Long> {

    @Query("select sc from SquadCategory sc inner join fetch sc.category where sc.squad.id in :squadIds")
    List<SquadCategory> fetchCategoriesBySquadIdIn(List<Long> squadIds);

    @Modifying
    @Query("delete from SquadCategory sc where sc.squad.id in :squadIds")
    int deleteBySquadIdIn(List<Long> squadIds);

}
