package revi1337.onsquad.category.application.initializer;

import static org.mockito.Mockito.verify;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.category.application.CachedCategoryService;

@ContextConfiguration(classes = {CategoryInitializer.class})
@ExtendWith(SpringExtension.class)
class CategoryInitializerTest {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @MockBean
    private CachedCategoryService cachedCategoryService;

    @SpyBean
    private CategoryInitializer categoryInitializer;

    @Test
    @DisplayName("ApplicationReadyEvent 가 호출되면 카테고리를 캐싱한다.")
    void success() {
        ApplicationReadyEvent readyEvent = new ApplicationReadyEvent(
                new SpringApplication(), new String[]{}, applicationContext, Duration.ofMinutes(1));

        applicationContext.publishEvent(readyEvent);

        verify(categoryInitializer).init();
        verify(cachedCategoryService).findCategories();
    }
}
