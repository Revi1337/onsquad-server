package revi1337.onsquad.auth.verification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import revi1337.onsquad.auth.verification.application.EmailVerificationRegistry;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

class ResilientEmailVerificationRegistryTest {

    private static final String CIRCUIT_BREAKER_NAME = "redisAuthCircuitBreaker";
    private static final int MINIMUM_NUMBER_OF_CALLS = 4;
    private static final String EMAIL = "user@test.com";
    private static final String CODE = "123456";
    private static final Duration DURATION = Duration.ofMinutes(5);

    private final Logger storageLogger = (Logger) LoggerFactory.getLogger(ResilientEmailVerificationRegistry.class);
    private final EmailVerificationRegistry redisStorage = mock(EmailVerificationRegistry.class);
    private final EmailVerificationRegistry rdbStorage = mock(EmailVerificationRegistry.class);

    private ListAppender<ILoggingEvent> appender;
    private CircuitBreaker circuitBreaker;
    private ResilientEmailVerificationRegistry storage;

    @BeforeEach
    void setUp() {
        appender = new ListAppender<>();
        appender.start();
        storageLogger.addAppender(appender);

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                .slidingWindowSize(MINIMUM_NUMBER_OF_CALLS)
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMinutes(5))
                .recordExceptions(DataAccessException.class)
                .build());
        circuitBreaker = registry.circuitBreaker(CIRCUIT_BREAKER_NAME);
        storage = new ResilientEmailVerificationRegistry(redisStorage, rdbStorage, registry);
    }

    @AfterEach
    void tearDown() {
        storageLogger.detachAppender(appender);
    }

    @Nested
    @DisplayName("Redis 가 정상인 경우")
    class WhenRedisIsHealthy {

        @Test
        @DisplayName("Redis 결과를 반환하고 RDB 는 호출하지 않는다")
        void returnsRedisResultWithoutTouchingRdb() {
            when(redisStorage.register(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).thenReturn(111L);
            when(redisStorage.isCodeMatched(EMAIL, CODE)).thenReturn(true);
            when(redisStorage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).thenReturn(true);
            when(redisStorage.confirm(EMAIL, CODE, DURATION)).thenReturn(true);
            when(redisStorage.hasStatus(EMAIL, VerificationStatus.SUCCESS)).thenReturn(true);

            assertThat(storage.register(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).isEqualTo(111L);
            assertThat(storage.isCodeMatched(EMAIL, CODE)).isTrue();
            assertThat(storage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isTrue();
            assertThat(storage.confirm(EMAIL, CODE, DURATION)).isTrue();
            assertThat(storage.hasStatus(EMAIL, VerificationStatus.SUCCESS)).isTrue();

            verifyNoInteractions(rdbStorage);
            assertThat(appender.list).isEmpty();
        }

        @Test
        @DisplayName("Redis 가 예외 없이 false 를 반환하면 정상 결과이므로 RDB 로 폴백하지 않는다")
        void doesNotFallBackOnNormalFalse() {
            when(redisStorage.isCodeMatched(EMAIL, CODE)).thenReturn(false);
            when(redisStorage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).thenReturn(false);
            when(redisStorage.confirm(EMAIL, CODE, DURATION)).thenReturn(false);
            when(redisStorage.hasStatus(EMAIL, VerificationStatus.SUCCESS)).thenReturn(false);
            stubRdbForAllMethods();

            assertThat(storage.isCodeMatched(EMAIL, CODE)).isFalse();
            assertThat(storage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isFalse();
            assertThat(storage.confirm(EMAIL, CODE, DURATION)).isFalse();
            assertThat(storage.hasStatus(EMAIL, VerificationStatus.SUCCESS)).isFalse();

            verifyNoInteractions(rdbStorage);
        }
    }

    @Nested
    @DisplayName("서킷 CLOSED 상태에서 Redis 가 실패하는 경우")
    class WhenRedisFails {

        @Test
        @DisplayName("저장은 RDB 결과를 반환한다")
        void saveFallsBack() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            long result = storage.register(EMAIL, CODE, VerificationStatus.PENDING, DURATION);

            assertThat(result).isEqualTo(777L);
            verify(rdbStorage).register(EMAIL, CODE, VerificationStatus.PENDING, DURATION);
        }

        @Test
        @DisplayName("코드 검증은 RDB 결과를 반환한다")
        void validateFallsBack() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            boolean result = storage.isCodeMatched(EMAIL, CODE);

            assertThat(result).isTrue();
            verify(rdbStorage).isCodeMatched(EMAIL, CODE);
        }

        @Test
        @DisplayName("상태 마킹은 RDB 결과를 반환한다")
        void markFallsBack() {
            failRedisForAllMethods(new QueryTimeoutException("redis timeout"));
            stubRdbForAllMethods();

            boolean result = storage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);

            assertThat(result).isTrue();
            verify(rdbStorage).changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);
        }

        @Test
        @DisplayName("인증 성공 마킹은 RDB 결과를 반환한다")
        void markAsSuccessFallsBack() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            boolean result = storage.confirm(EMAIL, CODE, DURATION);

            assertThat(result).isTrue();
            verify(rdbStorage).confirm(EMAIL, CODE, DURATION);
        }

        @Test
        @DisplayName("상태 확인은 RDB 결과를 반환한다")
        void isMarkedFallsBack() {
            failRedisForAllMethods(new QueryTimeoutException("redis timeout"));
            stubRdbForAllMethods();

            boolean result = storage.hasStatus(EMAIL, VerificationStatus.SUCCESS);

            assertThat(result).isTrue();
            verify(rdbStorage).hasStatus(EMAIL, VerificationStatus.SUCCESS);
        }

        @Test
        @DisplayName("DataAccessException 이 아닌 RuntimeException 도 RDB 로 폴백한다")
        void fallsBackOnAnyRuntimeException() {
            failRedisForAllMethods(new IllegalStateException("unexpected"));
            stubRdbForAllMethods();

            assertThat(storage.isCodeMatched(EMAIL, CODE)).isTrue();
            verify(rdbStorage).isCodeMatched(EMAIL, CODE);
        }

        @Test
        @DisplayName("일반 실패는 ERROR 레벨로 예외 타입과 스택트레이스를 남긴다")
        void logsErrorWithStackTrace() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            storage.isCodeMatched(EMAIL, CODE);

            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getFormattedMessage()).contains("RedisConnectionFailureException", "redis down");
            assertThat(event.getThrowableProxy()).isNotNull();
            assertThat(event.getThrowableProxy().getClassName()).contains("RedisConnectionFailureException");
        }

        @Test
        @DisplayName("Redis 실패는 서킷브레이커에 실패로 기록된다")
        void recordsRedisFailures() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            storage.isCodeMatched(EMAIL, CODE);
            storage.hasStatus(EMAIL, VerificationStatus.SUCCESS);

            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(2);
            assertThat(circuitBreaker.getState()).isEqualTo(State.CLOSED);
        }
    }

    @Nested
    @DisplayName("서킷 OPEN 상태")
    class WhenCircuitIsOpen {

        @BeforeEach
        void openCircuit() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();
            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS && circuitBreaker.getState() != State.OPEN; i++) {
                storage.isCodeMatched(EMAIL, CODE);
            }
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
            appender.list.clear();
            clearInvocations(redisStorage, rdbStorage);
        }

        @Test
        @DisplayName("5개 메서드 모두 Redis 를 호출하지 않고 RDB 결과를 반환한다")
        void doesNotTouchRedis() {
            assertThat(storage.register(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).isEqualTo(777L);
            assertThat(storage.isCodeMatched(EMAIL, CODE)).isTrue();
            assertThat(storage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isTrue();
            assertThat(storage.confirm(EMAIL, CODE, DURATION)).isTrue();
            assertThat(storage.hasStatus(EMAIL, VerificationStatus.SUCCESS)).isTrue();

            verifyNoInteractions(redisStorage);
            verify(rdbStorage, times(1)).register(EMAIL, CODE, VerificationStatus.PENDING, DURATION);
            verify(rdbStorage, times(1)).isCodeMatched(EMAIL, CODE);
            verify(rdbStorage, times(1)).changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);
            verify(rdbStorage, times(1)).confirm(EMAIL, CODE, DURATION);
            verify(rdbStorage, times(1)).hasStatus(EMAIL, VerificationStatus.SUCCESS);
        }

        @Test
        @DisplayName("차단은 WARN 레벨로 스택트레이스 없이 남긴다")
        void logsWarnWithoutStackTrace() {
            invokeAllMethods();

            assertThat(appender.list).hasSize(5);
            assertThat(appender.list).allSatisfy(event -> {
                assertThat(event.getLevel()).isEqualTo(Level.WARN);
                assertThat(event.getFormattedMessage()).contains("차단", CIRCUIT_BREAKER_NAME);
                assertThat(event.getThrowableProxy()).isNull();
            });
        }
    }

    @Nested
    @DisplayName("RDB 오류")
    class WhenRdbFails {

        @Test
        @DisplayName("Redis 가 정상이면 RDB 오류와 무관하게 Redis 결과를 반환한다")
        void doesNotTouchRdbWhenRedisSucceeds() {
            when(redisStorage.isCodeMatched(EMAIL, CODE)).thenReturn(true);
            when(rdbStorage.isCodeMatched(anyString(), anyString())).thenThrow(new DataRetrievalFailureException("mysql down"));

            assertThat(storage.isCodeMatched(EMAIL, CODE)).isTrue();

            verify(rdbStorage, never()).isCodeMatched(anyString(), anyString());
        }

        @Test
        @DisplayName("폴백 중 RDB 에서 난 예외는 그대로 전파된다")
        void propagatesRdbException() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            DataRetrievalFailureException rdbFailure = new DataRetrievalFailureException("mysql down");
            when(rdbStorage.register(anyString(), anyString(), any(), any())).thenThrow(rdbFailure);
            when(rdbStorage.isCodeMatched(anyString(), anyString())).thenThrow(rdbFailure);
            when(rdbStorage.changeStatus(anyString(), any(), any())).thenThrow(rdbFailure);
            when(rdbStorage.confirm(anyString(), anyString(), any())).thenThrow(rdbFailure);
            when(rdbStorage.hasStatus(anyString(), any())).thenThrow(rdbFailure);

            assertThatThrownBy(() -> storage.register(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.isCodeMatched(EMAIL, CODE)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.confirm(EMAIL, CODE, DURATION)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.hasStatus(EMAIL, VerificationStatus.SUCCESS)).isSameAs(rdbFailure);
        }

        @Test
        @DisplayName("RDB 예외는 서킷브레이커에 실패로 집계되지 않고 Redis 호출만 집계된다")
        void rdbExceptionIsNotRecordedAsFailure() {
            when(redisStorage.isCodeMatched(anyString(), anyString())).thenThrow(new RedisConnectionFailureException("redis down"));
            when(rdbStorage.isCodeMatched(anyString(), anyString())).thenThrow(new DataRetrievalFailureException("mysql down"));

            assertThatThrownBy(() -> storage.isCodeMatched(EMAIL, CODE)).isInstanceOf(DataRetrievalFailureException.class);

            assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls()).isEqualTo(1);
            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
            verify(redisStorage, times(1)).isCodeMatched(EMAIL, CODE);
            verify(rdbStorage, times(1)).isCodeMatched(EMAIL, CODE);
        }

        @Test
        @DisplayName("서킷이 열린 상태에서 RDB 예외가 반복돼도 실패 집계는 늘지 않고 상태도 유지된다")
        void rdbExceptionWhileOpenDoesNotChangeMetrics() {
            when(redisStorage.isCodeMatched(anyString(), anyString())).thenThrow(new RedisConnectionFailureException("redis down"));
            when(rdbStorage.isCodeMatched(anyString(), anyString())).thenThrow(new DataRetrievalFailureException("mysql down"));
            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
                assertThatThrownBy(() -> storage.isCodeMatched(EMAIL, CODE)).isInstanceOf(DataRetrievalFailureException.class);
            }
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
            int failedCallsWhenOpened = circuitBreaker.getMetrics().getNumberOfFailedCalls();
            clearInvocations(redisStorage);

            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
                assertThatThrownBy(() -> storage.isCodeMatched(EMAIL, CODE)).isInstanceOf(DataRetrievalFailureException.class);
            }

            verifyNoInteractions(redisStorage);
            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(failedCallsWhenOpened);
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
        }
    }

    private void failRedisForAllMethods(RuntimeException exception) {
        when(redisStorage.register(anyString(), anyString(), any(), any())).thenThrow(exception);
        when(redisStorage.isCodeMatched(anyString(), anyString())).thenThrow(exception);
        when(redisStorage.changeStatus(anyString(), any(), any())).thenThrow(exception);
        when(redisStorage.confirm(anyString(), anyString(), any())).thenThrow(exception);
        when(redisStorage.hasStatus(anyString(), any())).thenThrow(exception);
    }

    private void stubRdbForAllMethods() {
        when(rdbStorage.register(anyString(), anyString(), any(), any())).thenReturn(777L);
        when(rdbStorage.isCodeMatched(anyString(), anyString())).thenReturn(true);
        when(rdbStorage.changeStatus(anyString(), any(), any())).thenReturn(true);
        when(rdbStorage.confirm(anyString(), anyString(), any())).thenReturn(true);
        when(rdbStorage.hasStatus(anyString(), any())).thenReturn(true);
    }

    private void invokeAllMethods() {
        storage.register(EMAIL, CODE, VerificationStatus.PENDING, DURATION);
        storage.isCodeMatched(EMAIL, CODE);
        storage.changeStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);
        storage.confirm(EMAIL, CODE, DURATION);
        storage.hasStatus(EMAIL, VerificationStatus.SUCCESS);
    }
}
