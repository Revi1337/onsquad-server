package revi1337.onsquad.squad.config.category;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.squad.domain.category.CategoryRepository;

@RequiredArgsConstructor
@Configuration
public class CategoryInitializer {

    private final CategoryRepository categoryRepository;

    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        categoryRepository.findAll();
    }
}
