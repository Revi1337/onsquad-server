package revi1337.onsquad.infrastructure.support.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;

@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class RedisRequestCacheHandlerTest extends ApplicationLayerTestSupport {

    private static final String CIRCUIT_BREAKER_NAME = "redisThrottleCircuitBreaker";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisRequestCacheHandler redisRequestCacheHandler;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).reset();
    }

    @Nested
    @DisplayName("최초 요청 여부 확인")
    class IsFirstRequest {

        @Test
        @DisplayName("동일한 키로 연속 요청하면 첫 번째 요청만 true, 이후 요청은 false를 반환한다")
        void returnsTrueOnlyForFirstRequestWithSameKey() {
            String key = "request-cache:duplicate-key";
            String value = "value";

            Boolean first = redisRequestCacheHandler.isFirstRequest(key, value, 1, TimeUnit.MINUTES);
            Boolean second = redisRequestCacheHandler.isFirstRequest(key, value, 1, TimeUnit.MINUTES);

            assertSoftly(softly -> {
                softly.assertThat(first).isTrue();
                softly.assertThat(second).isFalse();
            });
        }

        @Test
        @DisplayName("서로 다른 키로 요청하면 각각 첫 요청으로 처리되어 모두 true를 반환한다")
        void returnsTrueForEachDistinctKey() {
            Boolean first = redisRequestCacheHandler.isFirstRequest("request-cache:key-1", "value", 1, TimeUnit.MINUTES);
            Boolean second = redisRequestCacheHandler.isFirstRequest("request-cache:key-2", "value", 1, TimeUnit.MINUTES);

            assertSoftly(softly -> {
                softly.assertThat(first).isTrue();
                softly.assertThat(second).isTrue();
            });
        }
    }

    @Nested
    @DisplayName("서킷브레이커 OPEN 상태")
    class CircuitBreakerOpen {

        @Test
        @DisplayName("서킷이 OPEN 상태면 Redis 호출 없이 CallNotPermittedException을 원인으로 하는 IllegalStateException을 던진다")
        void throwsIllegalStateExceptionCausedByCallNotPermitted_whenCircuitIsOpen() {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);
            circuitBreaker.transitionToOpenState();

            assertThatThrownBy(() -> redisRequestCacheHandler.isFirstRequest("request-cache:open-key", "value", 1, TimeUnit.MINUTES))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Redis 사용 불가")
                    .hasCauseInstanceOf(CallNotPermittedException.class);
        }
    }
}
