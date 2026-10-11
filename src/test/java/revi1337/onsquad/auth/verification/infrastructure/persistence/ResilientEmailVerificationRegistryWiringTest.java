package revi1337.onsquad.auth.verification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.verification.application.EmailVerificationValidator;
import revi1337.onsquad.auth.verification.application.EmailVerificationRegistry;

@SpringJUnitConfig(ResilientEmailVerificationRegistryWiringTest.WiringConfiguration.class)
class ResilientEmailVerificationRegistryWiringTest {

    @Autowired
    private EmailVerificationRegistry emailVerificationRegistry;

    @Autowired
    private EmailVerificationValidator emailVerificationValidator;

    @Test
    @DisplayName("EmailVerificationRegistry 타입 주입은 ResilientEmailVerificationRegistry 이다")
    void primaryIsResilient() {
        assertThat(emailVerificationRegistry).isInstanceOf(ResilientEmailVerificationRegistry.class);
    }

    @Test
    @DisplayName("ResilientEmailVerificationRegistry 에는 redis 필드에 Redis 구현체, rdb 필드에 RDB 구현체가 주입된다")
    void delegatesAreWiredByQualifier() {
        Object redis = ReflectionTestUtils.getField(emailVerificationRegistry, "redisEmailVerificationRegistry");
        Object rdb = ReflectionTestUtils.getField(emailVerificationRegistry, "rdbEmailVerificationRegistry");

        assertThat(redis).isInstanceOf(RedisEmailVerificationRegistry.class);
        assertThat(rdb).isInstanceOf(RdbEmailVerificationRegistry.class);
    }

    @Test
    @DisplayName("EmailVerificationValidator 는 ResilientEmailVerificationRegistry 를 주입받는다")
    void validatorReceivesResilient() {
        Object injected = ReflectionTestUtils.getField(emailVerificationValidator, "emailVerificationRegistry");

        assertThat(injected).isSameAs(emailVerificationRegistry);
    }

    @Configuration
    @ComponentScan(
            basePackageClasses = {ResilientEmailVerificationRegistry.class, EmailVerificationValidator.class},
            useDefaultFilters = false,
            includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                    ResilientEmailVerificationRegistry.class,
                    RedisEmailVerificationRegistry.class,
                    RdbEmailVerificationRegistry.class,
                    EmailVerificationValidator.class
            })
    )
    static class WiringConfiguration {

        @Bean
        StringRedisTemplate stringRedisTemplate() {
            return mock(StringRedisTemplate.class);
        }

        @Bean
        NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
            return mock(NamedParameterJdbcTemplate.class);
        }

        @Bean
        CircuitBreakerRegistry circuitBreakerRegistry() {
            return CircuitBreakerRegistry.ofDefaults();
        }
    }
}
