package revi1337.onsquad.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import revi1337.onsquad.auth.support.AuthenticateArgumentResolver;
import revi1337.onsquad.category.presentation.converter.CategoryConditionConverter;
import revi1337.onsquad.token.application.JsonWebTokenEvaluator;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JsonWebTokenEvaluator jsonWebTokenEvaluator;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticateArgumentResolver(jsonWebTokenEvaluator));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CategoryConditionConverter());
    }
}
