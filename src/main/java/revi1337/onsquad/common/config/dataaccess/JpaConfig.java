package revi1337.onsquad.common.config.dataaccess;

import com.blazebit.persistence.querydsl.JPQLNextTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLOps;
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
        return new JPAQueryFactory(new CustomJPQLTemplates(), entityManager);
    }

    static class CustomJPQLTemplates extends JPQLNextTemplates {

        public CustomJPQLTemplates() {
            add(SQLOps.ROWNUMBER, "row_number()");
        }
    }
}
