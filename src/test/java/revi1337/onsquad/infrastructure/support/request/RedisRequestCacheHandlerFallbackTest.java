package revi1337.onsquad.infrastructure.support.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;

@Import(RedisRequestCacheHandlerFallbackTest.BrokenFastRedisConfig.class)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class RedisRequestCacheHandlerFallbackTest extends ApplicationLayerTestSupport {

    private static final String CIRCUIT_BREAKER_NAME = "redisThrottleCircuitBreaker";

    @Autowired
    private RedisRequestCacheHandler redisRequestCacheHandler;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).reset();
    }

    @Test
    @DisplayName("Redis 연결이 불가능하면 DataAccessException을 원인으로 하는 IllegalStateException을 던진다")
    void throwsIllegalStateExceptionCausedByDataAccessException_whenRedisIsUnreachable() {
        assertThatThrownBy(() -> redisRequestCacheHandler.isFirstRequest("request-cache:unreachable-key", "value", 1, TimeUnit.MINUTES))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Redis 사용 불가")
                .hasCauseInstanceOf(DataAccessException.class);
    }

    @TestConfiguration
    static class BrokenFastRedisConfig {

        /**
         * 운영 {@code RedisConfig.fastStringRedisTemplate()} 빈을, 아무도 수신 대기하지 않는 포트로 바라보도록 덮어써서 Redis 연결 실패(DataAccessException)를
         * 강제로 유발한다. {@code spring.main.allow-bean-definition-overriding=true} (test application.yml) 설정으로 동일한 빈 이름을 재정의한다.
         */
        @Bean
        public StringRedisTemplate fastStringRedisTemplate() {
            RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration("127.0.0.1", 54321);
            LettuceConnectionFactory brokenConnectionFactory = new LettuceConnectionFactory(standaloneConfiguration);
            brokenConnectionFactory.afterPropertiesSet();
            return new StringRedisTemplate(brokenConnectionFactory);
        }
    }
}
