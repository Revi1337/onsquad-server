package revi1337.onsquad.category.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.category.application.CategoryService;

@RequiredArgsConstructor
@Configuration
public class CategoryInitializer {

    private final CategoryService cachedCategoryService;

    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        cachedCategoryService.findCategories();
    }
}
