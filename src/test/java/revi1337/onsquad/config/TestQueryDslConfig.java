package revi1337.onsquad.config;

import com.blazebit.persistence.querydsl.JPQLNextTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLOps;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestQueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(new CustomJPQLTemplates(), entityManager);
    }

    static class CustomJPQLTemplates extends JPQLNextTemplates {

        public CustomJPQLTemplates() {
            add(SQLOps.ROWNUMBER, "row_number()");
        }
    }
}
