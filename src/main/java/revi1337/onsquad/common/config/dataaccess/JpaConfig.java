package revi1337.onsquad.common.config.dataaccess;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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
            pageableResolver.setPageParameterName("page");
            pageableResolver.setSizeParameterName("size");
            pageableResolver.setOneIndexedParameters(true);
            pageableResolver.setMaxPageSize(100);
        };
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }
}
