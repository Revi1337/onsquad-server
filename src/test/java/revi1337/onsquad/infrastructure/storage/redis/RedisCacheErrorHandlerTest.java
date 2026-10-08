package revi1337.onsquad.infrastructure.storage.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.RedisConnectionFailureException;

class RedisCacheErrorHandlerTest {

    private final RedisCacheErrorHandler errorHandler = new RedisCacheErrorHandler();
    private Cache cache;

    @BeforeEach
    void setUp() {
        cache = mock(Cache.class);
        when(cache.getName()).thenReturn("crew-announce");
    }

    @Test
    @DisplayName("get 실패는 예외를 삼킨다")
    void swallowsGetError() {
        assertThatCode(() -> errorHandler.handleCacheGetError(new RedisConnectionFailureException("down"), cache, "key"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("put 실패는 예외를 삼킨다")
    void swallowsPutError() {
        assertThatCode(() -> errorHandler.handleCachePutError(new RedisConnectionFailureException("down"), cache, "key", "value"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("evict 실패는 예외를 삼킨다")
    void swallowsEvictError() {
        assertThatCode(() -> errorHandler.handleCacheEvictError(new RedisConnectionFailureException("down"), cache, "key"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("clear 실패는 예외를 삼킨다")
    void swallowsClearError() {
        CallNotPermittedException exception = CallNotPermittedException.createCallNotPermittedException(CircuitBreaker.ofDefaults("test"));

        assertThatCode(() -> errorHandler.handleCacheClearError(exception, cache)).doesNotThrowAnyException();
    }

    @Nested
    @DisplayName("로그 출력")
    class Logging {

        private final Logger handlerLogger = (Logger) LoggerFactory.getLogger(RedisCacheErrorHandler.class);
        private ListAppender<ILoggingEvent> appender;

        @BeforeEach
        void attachAppender() {
            appender = new ListAppender<>();
            appender.start();
            handlerLogger.addAppender(appender);
        }

        @AfterEach
        void detachAppender() {
            handlerLogger.detachAppender(appender);
        }

        @Test
        @DisplayName("일반 예외는 ERROR 레벨로 예외 타입을 남기고 스택트레이스를 포함한다")
        void logsErrorWithExceptionTypeAndStackTrace_whenCacheFails() {
            RedisConnectionFailureException exception = new RedisConnectionFailureException("down");

            errorHandler.handleCacheGetError(exception, cache, "key");

            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getFormattedMessage()).contains("get", "crew-announce", "key", "RedisConnectionFailureException", "down");
            assertThat(event.getThrowableProxy()).isNotNull();
            assertThat(event.getThrowableProxy().getClassName()).isEqualTo(RedisConnectionFailureException.class.getName());
        }

        @Test
        @DisplayName("래핑된 예외는 스택트레이스에 원인 예외가 포함된다")
        void includesCause_whenExceptionIsWrapped() {
            RedisConnectionFailureException exception = new RedisConnectionFailureException("down", new IllegalStateException("root"));

            errorHandler.handleCachePutError(exception, cache, "key", "value");

            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getThrowableProxy().getCause()).isNotNull();
            assertThat(event.getThrowableProxy().getCause().getClassName()).isEqualTo(IllegalStateException.class.getName());
        }

        @Test
        @DisplayName("서킷브레이커 차단 예외는 WARN 레벨로 스택트레이스 없이 남긴다")
        void logsWarnWithoutStackTrace_whenCircuitBreakerIsOpen() {
            CallNotPermittedException exception = CallNotPermittedException.createCallNotPermittedException(CircuitBreaker.ofDefaults("test"));

            errorHandler.handleCacheGetError(exception, cache, "key");

            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.WARN);
            assertThat(event.getFormattedMessage()).contains("get", "crew-announce", "key");
            assertThat(event.getThrowableProxy()).isNull();
        }
    }
}
