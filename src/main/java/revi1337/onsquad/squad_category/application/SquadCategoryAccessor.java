package revi1337.onsquad.squad_category.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_category.domain.SquadCategories;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;

@Component
@RequiredArgsConstructor
public class SquadCategoryAccessor {

    private final SquadCategoryRepository squadCategoryRepository;

    public SquadCategories fetchCategoriesBySquadIdIn(List<Long> squadIds) {
        return new SquadCategories(squadCategoryRepository.fetchCategoriesBySquadIdIn(squadIds));
    }
}
