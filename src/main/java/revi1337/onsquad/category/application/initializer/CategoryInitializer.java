package revi1337.onsquad.category.application.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.category.application.CategoryService;

@Component
@RequiredArgsConstructor
public class CategoryInitializer {

    private final CategoryService cachedCategoryService;

    @EventListener(ApplicationReadyEvent.class)
    void init() {
        cachedCategoryService.findCategories();
    }
}
