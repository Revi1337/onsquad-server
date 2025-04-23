package revi1337.onsquad.common.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@TestConfiguration
public class ApplicationLayerConfiguration {

    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new DefaultHandlerExceptionResolver();
    }
}
