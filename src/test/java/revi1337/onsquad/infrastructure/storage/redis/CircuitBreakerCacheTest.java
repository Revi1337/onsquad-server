package revi1337.onsquad.infrastructure.storage.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueRetrievalException;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;

class CircuitBreakerCacheTest {

    private Cache delegate;
    private CircuitBreaker circuitBreaker;
    private CircuitBreakerCache circuitBreakerCache;

    @BeforeEach
    void setUp() {
        delegate = mock(Cache.class);
        circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(2)
                .failureRateThreshold(50)
                .recordExceptions(DataAccessException.class)
                .build());
        circuitBreakerCache = new CircuitBreakerCache(delegate, circuitBreaker);
    }

    @Nested
    @DisplayName("정상 경로")
    class Closed {

        @Test
        @DisplayName("get/put/evict/clear 는 delegate 로 그대로 위임된다")
        void delegatesToUnderlyingCache() {
            when(delegate.get("key")).thenReturn(new SimpleValueWrapper("value"));

            circuitBreakerCache.put("key", "value");
            Cache.ValueWrapper wrapper = circuitBreakerCache.get("key");
            circuitBreakerCache.evict("key");
            circuitBreakerCache.clear();

            assertThat(wrapper).isNotNull();
            assertThat(wrapper.get()).isEqualTo("value");
            verify(delegate).put("key", "value");
            verify(delegate).evict("key");
            verify(delegate).clear();
        }

        @Test
        @DisplayName("getName 은 delegate 의 이름을 반환한다")
        void returnsDelegateName() {
            when(delegate.getName()).thenReturn("crew-announce");

            assertThat(circuitBreakerCache.getName()).isEqualTo("crew-announce");
        }

        @Test
        @DisplayName("delegate 예외는 삼키지 않고 그대로 던진다")
        void rethrowsDelegateException() {
            RedisConnectionFailureException exception = new RedisConnectionFailureException("down");
            when(delegate.get("key")).thenThrow(exception);

            assertThatThrownBy(() -> circuitBreakerCache.get("key")).isSameAs(exception);
        }
    }

    @Nested
    @DisplayName("서킷 OPEN 상태")
    class Open {

        @BeforeEach
        void openCircuit() {
            circuitBreaker.transitionToOpenState();
        }

        @Test
        @DisplayName("모든 Redis I/O 는 delegate 호출 없이 CallNotPermittedException 을 던진다")
        void throwsCallNotPermittedWithoutTouchingDelegate() {
            assertThatThrownBy(() -> circuitBreakerCache.get("key")).isInstanceOf(CallNotPermittedException.class);
            assertThatThrownBy(() -> circuitBreakerCache.get("key", String.class)).isInstanceOf(CallNotPermittedException.class);
            assertThatThrownBy(() -> circuitBreakerCache.put("key", "value")).isInstanceOf(CallNotPermittedException.class);
            assertThatThrownBy(() -> circuitBreakerCache.putIfAbsent("key", "value")).isInstanceOf(CallNotPermittedException.class);
            assertThatThrownBy(() -> circuitBreakerCache.evict("key")).isInstanceOf(CallNotPermittedException.class);
            assertThatThrownBy(() -> circuitBreakerCache.evictIfPresent("key")).isInstanceOf(CallNotPermittedException.class);
            assertThatThrownBy(() -> circuitBreakerCache.clear()).isInstanceOf(CallNotPermittedException.class);
            assertThatThrownBy(() -> circuitBreakerCache.invalidate()).isInstanceOf(CallNotPermittedException.class);

            verifyNoInteractions(delegate);
        }

        @Test
        @DisplayName("sync 조회(get with loader)도 loader 를 실행하지 않고 CallNotPermittedException 을 던진다")
        void syncGetThrowsCallNotPermittedWithoutInvokingLoader() {
            AtomicInteger loaderCalls = new AtomicInteger();

            assertThatThrownBy(() -> circuitBreakerCache.get("key", () -> loaderCalls.incrementAndGet()))
                    .isInstanceOf(CallNotPermittedException.class);

            assertThat(loaderCalls).hasValue(0);
        }
    }

    @Nested
    @DisplayName("실패 집계")
    class FailureRecording {

        @Test
        @DisplayName("DataAccessException 이 임계치를 넘으면 서킷이 OPEN 된다")
        void opensCircuit_whenDataAccessExceptionExceedsThreshold() {
            when(delegate.get("key")).thenThrow(new QueryTimeoutException("timeout"));

            assertThatThrownBy(() -> circuitBreakerCache.get("key")).isInstanceOf(QueryTimeoutException.class);
            assertThatThrownBy(() -> circuitBreakerCache.get("key")).isInstanceOf(QueryTimeoutException.class);

            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
            assertThatThrownBy(() -> circuitBreakerCache.get("key")).isInstanceOf(CallNotPermittedException.class);
        }
    }

    @Nested
    @DisplayName("sync 조회 (get with loader)")
    class SyncGet {

        @Test
        @DisplayName("캐시 hit 이면 loader 를 실행하지 않고 값을 반환한다")
        void returnsCachedValue_withoutInvokingLoader() {
            AtomicInteger loaderCalls = new AtomicInteger();
            when(delegate.get("key")).thenReturn(new SimpleValueWrapper("cached"));

            String result = circuitBreakerCache.get("key", () -> {
                loaderCalls.incrementAndGet();
                return "loaded";
            });

            assertThat(result).isEqualTo("cached");
            assertThat(loaderCalls).hasValue(0);
        }

        @Test
        @DisplayName("캐시 miss 이면 loader 결과를 반환하고 캐시에 저장한다")
        void loadsAndPuts_whenMiss() {
            when(delegate.get("key")).thenReturn(null);

            String result = circuitBreakerCache.get("key", () -> "loaded");

            assertThat(result).isEqualTo("loaded");
            verify(delegate).put("key", "loaded");
        }

        @Test
        @DisplayName("loader(DB)에서 발생한 DataAccessException 은 서킷 실패로 집계되지 않고 ValueRetrievalException 으로 감싸진다")
        void loaderFailureIsNotRecordedByCircuitBreaker() {
            when(delegate.get("key")).thenReturn(null);

            for (int i = 0; i < 5; i++) {
                assertThatThrownBy(() -> circuitBreakerCache.get("key", () -> {
                    throw new QueryTimeoutException("db timeout");
                }))
                        .isInstanceOf(ValueRetrievalException.class)
                        .hasCauseInstanceOf(QueryTimeoutException.class);
            }

            assertThat(circuitBreaker.getState()).isEqualTo(State.CLOSED);
            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
        }
    }
}
