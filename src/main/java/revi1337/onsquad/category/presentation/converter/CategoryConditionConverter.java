package revi1337.onsquad.category.presentation.converter;

import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.category.presentation.request.CategoryCondition;

public class CategoryConditionConverter implements Converter<String, CategoryCondition> {

    @Override
    public CategoryCondition convert(String categoryText) {
        return Optional.ofNullable(CategoryType.fromText(categoryText))
                .map(CategoryCondition::new)
                .orElse(new CategoryCondition(CategoryType.ALL));
    }
}
