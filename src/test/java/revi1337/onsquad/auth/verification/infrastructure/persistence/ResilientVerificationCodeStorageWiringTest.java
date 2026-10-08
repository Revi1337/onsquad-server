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
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;

@SpringJUnitConfig(ResilientVerificationCodeStorageWiringTest.WiringConfiguration.class)
class ResilientVerificationCodeStorageWiringTest {

    @Autowired
    private VerificationCodeStorage verificationCodeStorage;

    @Autowired
    private EmailVerificationValidator emailVerificationValidator;

    @Test
    @DisplayName("VerificationCodeStorage 타입 주입은 ResilientVerificationCodeStorage 이다")
    void primaryIsResilient() {
        assertThat(verificationCodeStorage).isInstanceOf(ResilientVerificationCodeStorage.class);
    }

    @Test
    @DisplayName("ResilientVerificationCodeStorage 에는 redis 필드에 Redis 구현체, rdb 필드에 RDB 구현체가 주입된다")
    void delegatesAreWiredByQualifier() {
        Object redis = ReflectionTestUtils.getField(verificationCodeStorage, "redisVerificationCodeStorage");
        Object rdb = ReflectionTestUtils.getField(verificationCodeStorage, "rdbVerificationCodeStorage");

        assertThat(redis).isInstanceOf(RedisVerificationCodeStorage.class);
        assertThat(rdb).isInstanceOf(RdbVerificationCodeStorage.class);
    }

    @Test
    @DisplayName("EmailVerificationValidator 는 ResilientVerificationCodeStorage 를 주입받는다")
    void validatorReceivesResilient() {
        Object injected = ReflectionTestUtils.getField(emailVerificationValidator, "verificationCodeStorage");

        assertThat(injected).isSameAs(verificationCodeStorage);
    }

    @Configuration
    @ComponentScan(
            basePackageClasses = {ResilientVerificationCodeStorage.class, EmailVerificationValidator.class},
            useDefaultFilters = false,
            includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                    ResilientVerificationCodeStorage.class,
                    RedisVerificationCodeStorage.class,
                    RdbVerificationCodeStorage.class,
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
