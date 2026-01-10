package revi1337.onsquad.squad_category.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.squad_category.domain.result.SquadCategoryResult;

@Getter
public class SquadCategories {

    private final List<SquadCategoryResult> categories;

    public SquadCategories(List<SquadCategoryResult> categories) {
        this.categories = Collections.unmodifiableList(categories);
    }

    public Map<Long, List<CategoryType>> groupBySquadId() {
        return categories.stream()
                .collect(Collectors.groupingBy(
                        SquadCategoryResult::squadId,
                        Collectors.mapping(SquadCategoryResult::categoryType, Collectors.toList())
                ));
    }

    public Map<Long, SquadCategories> splitBySquad() {
        return categories.stream()
                .collect(Collectors.groupingBy(
                        SquadCategoryResult::squadId,
                        Collectors.collectingAndThen(Collectors.toList(), SquadCategories::new)
                ));
    }

    public List<SquadCategoryResult> values() {
        return categories;
    }
}
