package revi1337.onsquad.squad.application.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.domain.category.CategoryRepository;
import revi1337.onsquad.squad.dto.category.CategoryDto;

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
