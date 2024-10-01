package revi1337.onsquad.category.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.domain.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DefaultCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<String> findCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> category.getCategoryType().getText())
                .collect(Collectors.toList());
    }
}
