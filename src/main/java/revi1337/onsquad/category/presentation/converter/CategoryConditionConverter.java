package revi1337.onsquad.category.presentation.converter;

import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;

public class CategoryConditionConverter implements Converter<String, CategoryCondition> {

    @Override
    public CategoryCondition convert(String categoryText) {
        return Optional.ofNullable(CategoryType.mapFromText(categoryText))
                .map(CategoryCondition::new)
                .orElse(new CategoryCondition(CategoryType.ALL));
    }
}
