package revi1337.onsquad.auth.token.infrastructure.persistence;

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
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.auth.token.application.JsonWebTokenEvaluator;
import revi1337.onsquad.auth.token.application.JsonWebTokenManager;
import revi1337.onsquad.auth.token.application.JsonWebTokenProvider;
import revi1337.onsquad.auth.token.application.RefreshTokenStorage;
import revi1337.onsquad.auth.token.infrastructure.TokenProperties;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.member.application.listener.MemberContextEventListener;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;

@SpringJUnitConfig(ResilientRefreshTokenStorageWiringTest.WiringConfiguration.class)
class ResilientRefreshTokenStorageWiringTest {

    @Autowired
    private RefreshTokenStorage refreshTokenStorage;

    @Autowired
    private JsonWebTokenManager jsonWebTokenManager;

    @Autowired
    private MemberContextEventListener memberContextEventListener;

    @Test
    @DisplayName("RefreshTokenStorage 타입 주입은 ResilientRefreshTokenStorage 이다")
    void primaryIsResilient() {
        assertThat(refreshTokenStorage).isInstanceOf(ResilientRefreshTokenStorage.class);
    }

    @Test
    @DisplayName("ResilientRefreshTokenStorage 에는 redis 필드에 Redis 구현체, rdb 필드에 RDB 구현체가 주입된다")
    void delegatesAreWiredByQualifier() {
        Object redis = ReflectionTestUtils.getField(refreshTokenStorage, "redisRefreshTokenStorage");
        Object rdb = ReflectionTestUtils.getField(refreshTokenStorage, "rdbRefreshTokenStorage");

        assertThat(redis).isInstanceOf(RedisRefreshTokenStorage.class);
        assertThat(rdb).isInstanceOf(RdbRefreshTokenStorage.class);
    }

    @Test
    @DisplayName("JsonWebTokenManager 는 ResilientRefreshTokenStorage 를 주입받는다")
    void jsonWebTokenManagerReceivesResilient() {
        Object injected = ReflectionTestUtils.getField(jsonWebTokenManager, "refreshTokenStorage");

        assertThat(injected).isSameAs(refreshTokenStorage);
    }

    @Test
    @DisplayName("MemberContextEventListener 는 ResilientRefreshTokenStorage 를 주입받는다")
    void memberContextEventListenerReceivesResilient() {
        Object injected = ReflectionTestUtils.getField(memberContextEventListener, "refreshTokenStorage");

        assertThat(injected).isSameAs(refreshTokenStorage);
    }

    @Configuration
    @ComponentScan(
            basePackageClasses = {ResilientRefreshTokenStorage.class, JsonWebTokenManager.class, MemberContextEventListener.class},
            useDefaultFilters = false,
            includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                    ResilientRefreshTokenStorage.class,
                    RedisRefreshTokenStorage.class,
                    RdbRefreshTokenStorage.class,
                    JsonWebTokenManager.class,
                    MemberContextEventListener.class
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

        @Bean
        TokenProperties tokenProperties() {
            return mock(TokenProperties.class);
        }

        @Bean
        JsonWebTokenProvider jsonWebTokenProvider() {
            return mock(JsonWebTokenProvider.class);
        }

        @Bean
        JsonWebTokenEvaluator jsonWebTokenEvaluator() {
            return mock(JsonWebTokenEvaluator.class);
        }

        @Bean
        HistoryRepository historyRepository() {
            return mock(HistoryRepository.class);
        }

        @Bean
        NotificationRepository notificationRepository() {
            return mock(NotificationRepository.class);
        }

        @Bean
        AnnounceCacheService announceCacheService() {
            return mock(AnnounceCacheService.class);
        }
    }
}
