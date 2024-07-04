package revi1337.onsquad.config;

import com.blazebit.persistence.querydsl.JPQLNextOps;
import com.blazebit.persistence.querydsl.JPQLNextTemplates;
import com.querydsl.jpa.Hibernate5Templates;
import com.querydsl.jpa.JPQLTemplates;
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
//        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
//        return new JPAQueryFactory(JPQLNextTemplates.DEFAULT, entityManager);
        return new JPAQueryFactory(new CustomJPQLTemplates(), entityManager);
    }

//    static class CustomJPQLTemplates extends JPQLTemplates {
//    static class CustomJPQLTemplates extends Hibernate5Templates {
    static class CustomJPQLTemplates extends JPQLNextTemplates {

        public CustomJPQLTemplates() {
            add(SQLOps.ROWNUMBER, "row_number()");
            add(JPQLNextOps.ROW_NUMBER, "row_number()");
            add(JPQLNextOps.WINDOW_ORDER_BY, "ORDER BY {0}");
            add(JPQLNextOps.WINDOW_PARTITION_BY, "PARTITION BY {0}");
            add(JPQLNextOps.WINDOW_DEFINITION_1, "{0}");
            add(JPQLNextOps.WINDOW_DEFINITION_2, "{0} {1}");
            add(JPQLNextOps.WINDOW_DEFINITION_3, "{0} {1} {2}");
            add(JPQLNextOps.WINDOW_DEFINITION_4, "{0} {1} {2} {3}");
        }
    }
}
