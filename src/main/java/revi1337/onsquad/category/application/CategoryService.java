package revi1337.onsquad.category.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.CategoryRepository;
import revi1337.onsquad.category.application.dto.CategoryDto;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> findCategories() {
        return categoryRepository.findAllCategories().stream()
                .map(CategoryDto::from)
                .collect(Collectors.toList());
    }
}
