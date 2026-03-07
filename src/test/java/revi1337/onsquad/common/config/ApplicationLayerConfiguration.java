package revi1337.onsquad.common.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@TestConfiguration
public class ApplicationLayerConfiguration {

    @MockBean
    public SortHandlerMethodArgumentResolver sortResolver;

    @MockBean
    public SortHandlerMethodArgumentResolverCustomizer pagingCustomizer;

    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new ExceptionHandlerExceptionResolver();
    }
}
