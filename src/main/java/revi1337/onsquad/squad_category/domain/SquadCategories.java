package revi1337.onsquad.squad_category.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;

public class SquadCategories {

    private final List<SquadCategory> categories;

    public SquadCategories(List<SquadCategory> categories) {
        this.categories = Collections.unmodifiableList(categories);
    }

    public Map<Long, List<CategoryType>> groupBySquadId() {
        return categories.stream()
                .collect(Collectors.groupingBy(
                        sc -> sc.getSquad().getId(),
                        Collectors.mapping(
                                sc -> sc.getCategory().getCategoryType(),
                                Collectors.toList()
                        )
                ));
    }
}
