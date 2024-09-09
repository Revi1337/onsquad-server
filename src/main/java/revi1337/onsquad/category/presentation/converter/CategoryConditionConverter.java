package revi1337.onsquad.squad.controller.category.converter;

import org.springframework.core.convert.converter.Converter;
import revi1337.onsquad.squad.domain.vo.category.CategoryType;
import revi1337.onsquad.squad.dto.request.CategoryCondition;

import java.util.Optional;

public class CategoryConditionConverter implements Converter<String, CategoryCondition> {

    @Override
    public CategoryCondition convert(String categoryText) {
        return Optional.ofNullable(CategoryType.mapFromText(categoryText))
                .map(CategoryCondition::new)
                .orElse(new CategoryCondition(CategoryType.ALL));
    }
}
