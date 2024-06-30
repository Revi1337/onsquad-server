package revi1337.onsquad.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customPageableResolver() {
        return pageableResolver -> {
            pageableResolver.setQualifierDelimiter("");
            pageableResolver.setPageParameterName("Page");
            pageableResolver.setSizeParameterName("Size");
            pageableResolver.setOneIndexedParameters(true);
        };
    }
}
