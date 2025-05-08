package revi1337.onsquad.squad_category.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.category.domain.vo.CategoryType;

public interface SquadCategoryJpaRepository extends JpaRepository<SquadCategory, Long> {

    List<SquadCategory> findAllBySquadId(Long squadId);

    @Query("select c.categoryType from SquadCategory sc inner join sc.category c where sc.squad.id = :squadId")
    List<CategoryType> fetchAllBySquadId(Long squadId);

}
