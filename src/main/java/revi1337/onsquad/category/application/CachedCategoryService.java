package revi1337.onsquad.category.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CachedCategoryService implements CategoryService {

    private final CategoryService defaultCategoryService;
    private final List<String> cachedCategories = new ArrayList<>();

    @Override
    public List<String> findCategories() {
        if (!cachedCategories.isEmpty()) {
            log.info("[Cache Hit] Categories Cache Hit");
            return cachedCategories;
        }

        cachedCategories.addAll(defaultCategoryService.findCategories());
        return cachedCategories;
    }
}
