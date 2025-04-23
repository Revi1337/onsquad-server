package revi1337.onsquad.common.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.blazebit.persistence.querydsl.JPQLNextTemplates;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLOps;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class PersistenceLayerConfiguration {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(new CustomJPQLTemplates(), entityManagerFactory.createEntityManager());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    public CriteriaBuilderFactory createCriteriaBuilderFactory() {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    @Bean
    public BlazeJPAQueryFactory blazeJPAQueryFactory() {
        return new BlazeJPAQueryFactory(entityManagerFactory.createEntityManager(), createCriteriaBuilderFactory());
    }

    static class CustomJPQLTemplates extends JPQLNextTemplates {

        public CustomJPQLTemplates() {
            add(SQLOps.ROWNUMBER, "row_number()");
        }
    }
}