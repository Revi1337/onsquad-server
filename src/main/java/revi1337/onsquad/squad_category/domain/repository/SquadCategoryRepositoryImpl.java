package revi1337.onsquad.squad_category.domain.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory;

@Repository
@RequiredArgsConstructor
public class SquadCategoryRepositoryImpl implements SquadCategoryRepository {

    private final SquadCategoryJdbcRepository squadCategoryJdbcRepository;
    private final SquadCategoryJpaRepository squadCategoryJpaRepository;

    @Override
    public int insertBatch(Long squadId, List<Category> categories) {
        return squadCategoryJdbcRepository.insertBatch(squadId, categories);
    }

    @Override
    public List<SimpleSquadCategory> fetchCategoriesBySquadIdIn(List<Long> squadIds) {
        return squadCategoryJpaRepository.fetchCategoriesBySquadIdIn(squadIds);
    }

    @Override
    public int deleteBySquadIdIn(List<Long> squadIds) {
        return squadCategoryJpaRepository.deleteBySquadIdIn(squadIds);
    }
}
