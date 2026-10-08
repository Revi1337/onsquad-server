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
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

class ResilientVerificationCodeStorageTest {

    private static final String CIRCUIT_BREAKER_NAME = "redisAuthCircuitBreaker";
    private static final int MINIMUM_NUMBER_OF_CALLS = 4;
    private static final String EMAIL = "user@test.com";
    private static final String CODE = "123456";
    private static final Duration DURATION = Duration.ofMinutes(5);

    private final Logger storageLogger = (Logger) LoggerFactory.getLogger(ResilientVerificationCodeStorage.class);
    private final VerificationCodeStorage redisStorage = mock(VerificationCodeStorage.class);
    private final VerificationCodeStorage rdbStorage = mock(VerificationCodeStorage.class);

    private ListAppender<ILoggingEvent> appender;
    private CircuitBreaker circuitBreaker;
    private ResilientVerificationCodeStorage storage;

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
        storage = new ResilientVerificationCodeStorage(redisStorage, rdbStorage, registry);
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
            when(redisStorage.saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).thenReturn(111L);
            when(redisStorage.isValidVerificationCode(EMAIL, CODE)).thenReturn(true);
            when(redisStorage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).thenReturn(true);
            when(redisStorage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION)).thenReturn(true);
            when(redisStorage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS)).thenReturn(true);

            assertThat(storage.saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).isEqualTo(111L);
            assertThat(storage.isValidVerificationCode(EMAIL, CODE)).isTrue();
            assertThat(storage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isTrue();
            assertThat(storage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION)).isTrue();
            assertThat(storage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS)).isTrue();

            verifyNoInteractions(rdbStorage);
            assertThat(appender.list).isEmpty();
        }

        @Test
        @DisplayName("Redis 가 예외 없이 false 를 반환하면 정상 결과이므로 RDB 로 폴백하지 않는다")
        void doesNotFallBackOnNormalFalse() {
            when(redisStorage.isValidVerificationCode(EMAIL, CODE)).thenReturn(false);
            when(redisStorage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).thenReturn(false);
            when(redisStorage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION)).thenReturn(false);
            when(redisStorage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS)).thenReturn(false);
            stubRdbForAllMethods();

            assertThat(storage.isValidVerificationCode(EMAIL, CODE)).isFalse();
            assertThat(storage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isFalse();
            assertThat(storage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION)).isFalse();
            assertThat(storage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS)).isFalse();

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

            long result = storage.saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION);

            assertThat(result).isEqualTo(777L);
            verify(rdbStorage).saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION);
        }

        @Test
        @DisplayName("코드 검증은 RDB 결과를 반환한다")
        void validateFallsBack() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            boolean result = storage.isValidVerificationCode(EMAIL, CODE);

            assertThat(result).isTrue();
            verify(rdbStorage).isValidVerificationCode(EMAIL, CODE);
        }

        @Test
        @DisplayName("상태 마킹은 RDB 결과를 반환한다")
        void markFallsBack() {
            failRedisForAllMethods(new QueryTimeoutException("redis timeout"));
            stubRdbForAllMethods();

            boolean result = storage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);

            assertThat(result).isTrue();
            verify(rdbStorage).markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);
        }

        @Test
        @DisplayName("인증 성공 마킹은 RDB 결과를 반환한다")
        void markAsSuccessFallsBack() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            boolean result = storage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION);

            assertThat(result).isTrue();
            verify(rdbStorage).markVerificationStatusAsSuccess(EMAIL, CODE, DURATION);
        }

        @Test
        @DisplayName("상태 확인은 RDB 결과를 반환한다")
        void isMarkedFallsBack() {
            failRedisForAllMethods(new QueryTimeoutException("redis timeout"));
            stubRdbForAllMethods();

            boolean result = storage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS);

            assertThat(result).isTrue();
            verify(rdbStorage).isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS);
        }

        @Test
        @DisplayName("DataAccessException 이 아닌 RuntimeException 도 RDB 로 폴백한다")
        void fallsBackOnAnyRuntimeException() {
            failRedisForAllMethods(new IllegalStateException("unexpected"));
            stubRdbForAllMethods();

            assertThat(storage.isValidVerificationCode(EMAIL, CODE)).isTrue();
            verify(rdbStorage).isValidVerificationCode(EMAIL, CODE);
        }

        @Test
        @DisplayName("일반 실패는 ERROR 레벨로 예외 타입과 스택트레이스를 남긴다")
        void logsErrorWithStackTrace() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            stubRdbForAllMethods();

            storage.isValidVerificationCode(EMAIL, CODE);

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

            storage.isValidVerificationCode(EMAIL, CODE);
            storage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS);

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
                storage.isValidVerificationCode(EMAIL, CODE);
            }
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
            appender.list.clear();
            clearInvocations(redisStorage, rdbStorage);
        }

        @Test
        @DisplayName("5개 메서드 모두 Redis 를 호출하지 않고 RDB 결과를 반환한다")
        void doesNotTouchRedis() {
            assertThat(storage.saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).isEqualTo(777L);
            assertThat(storage.isValidVerificationCode(EMAIL, CODE)).isTrue();
            assertThat(storage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isTrue();
            assertThat(storage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION)).isTrue();
            assertThat(storage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS)).isTrue();

            verifyNoInteractions(redisStorage);
            verify(rdbStorage, times(1)).saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION);
            verify(rdbStorage, times(1)).isValidVerificationCode(EMAIL, CODE);
            verify(rdbStorage, times(1)).markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);
            verify(rdbStorage, times(1)).markVerificationStatusAsSuccess(EMAIL, CODE, DURATION);
            verify(rdbStorage, times(1)).isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS);
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
            when(redisStorage.isValidVerificationCode(EMAIL, CODE)).thenReturn(true);
            when(rdbStorage.isValidVerificationCode(anyString(), anyString())).thenThrow(new DataRetrievalFailureException("mysql down"));

            assertThat(storage.isValidVerificationCode(EMAIL, CODE)).isTrue();

            verify(rdbStorage, never()).isValidVerificationCode(anyString(), anyString());
        }

        @Test
        @DisplayName("폴백 중 RDB 에서 난 예외는 그대로 전파된다")
        void propagatesRdbException() {
            failRedisForAllMethods(new RedisConnectionFailureException("redis down"));
            DataRetrievalFailureException rdbFailure = new DataRetrievalFailureException("mysql down");
            when(rdbStorage.saveVerificationCode(anyString(), anyString(), any(), any())).thenThrow(rdbFailure);
            when(rdbStorage.isValidVerificationCode(anyString(), anyString())).thenThrow(rdbFailure);
            when(rdbStorage.markVerificationStatus(anyString(), any(), any())).thenThrow(rdbFailure);
            when(rdbStorage.markVerificationStatusAsSuccess(anyString(), anyString(), any())).thenThrow(rdbFailure);
            when(rdbStorage.isMarkedVerificationStatusWith(anyString(), any())).thenThrow(rdbFailure);

            assertThatThrownBy(() -> storage.saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.isValidVerificationCode(EMAIL, CODE)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION)).isSameAs(rdbFailure);
            assertThatThrownBy(() -> storage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS)).isSameAs(rdbFailure);
        }

        @Test
        @DisplayName("RDB 예외는 서킷브레이커에 실패로 집계되지 않고 Redis 호출만 집계된다")
        void rdbExceptionIsNotRecordedAsFailure() {
            when(redisStorage.isValidVerificationCode(anyString(), anyString())).thenThrow(new RedisConnectionFailureException("redis down"));
            when(rdbStorage.isValidVerificationCode(anyString(), anyString())).thenThrow(new DataRetrievalFailureException("mysql down"));

            assertThatThrownBy(() -> storage.isValidVerificationCode(EMAIL, CODE)).isInstanceOf(DataRetrievalFailureException.class);

            assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls()).isEqualTo(1);
            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
            verify(redisStorage, times(1)).isValidVerificationCode(EMAIL, CODE);
            verify(rdbStorage, times(1)).isValidVerificationCode(EMAIL, CODE);
        }

        @Test
        @DisplayName("서킷이 열린 상태에서 RDB 예외가 반복돼도 실패 집계는 늘지 않고 상태도 유지된다")
        void rdbExceptionWhileOpenDoesNotChangeMetrics() {
            when(redisStorage.isValidVerificationCode(anyString(), anyString())).thenThrow(new RedisConnectionFailureException("redis down"));
            when(rdbStorage.isValidVerificationCode(anyString(), anyString())).thenThrow(new DataRetrievalFailureException("mysql down"));
            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
                assertThatThrownBy(() -> storage.isValidVerificationCode(EMAIL, CODE)).isInstanceOf(DataRetrievalFailureException.class);
            }
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
            int failedCallsWhenOpened = circuitBreaker.getMetrics().getNumberOfFailedCalls();
            clearInvocations(redisStorage);

            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
                assertThatThrownBy(() -> storage.isValidVerificationCode(EMAIL, CODE)).isInstanceOf(DataRetrievalFailureException.class);
            }

            verifyNoInteractions(redisStorage);
            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(failedCallsWhenOpened);
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
        }
    }

    private void failRedisForAllMethods(RuntimeException exception) {
        when(redisStorage.saveVerificationCode(anyString(), anyString(), any(), any())).thenThrow(exception);
        when(redisStorage.isValidVerificationCode(anyString(), anyString())).thenThrow(exception);
        when(redisStorage.markVerificationStatus(anyString(), any(), any())).thenThrow(exception);
        when(redisStorage.markVerificationStatusAsSuccess(anyString(), anyString(), any())).thenThrow(exception);
        when(redisStorage.isMarkedVerificationStatusWith(anyString(), any())).thenThrow(exception);
    }

    private void stubRdbForAllMethods() {
        when(rdbStorage.saveVerificationCode(anyString(), anyString(), any(), any())).thenReturn(777L);
        when(rdbStorage.isValidVerificationCode(anyString(), anyString())).thenReturn(true);
        when(rdbStorage.markVerificationStatus(anyString(), any(), any())).thenReturn(true);
        when(rdbStorage.markVerificationStatusAsSuccess(anyString(), anyString(), any())).thenReturn(true);
        when(rdbStorage.isMarkedVerificationStatusWith(anyString(), any())).thenReturn(true);
    }

    private void invokeAllMethods() {
        storage.saveVerificationCode(EMAIL, CODE, VerificationStatus.PENDING, DURATION);
        storage.isValidVerificationCode(EMAIL, CODE);
        storage.markVerificationStatus(EMAIL, VerificationStatus.SUCCESS, DURATION);
        storage.markVerificationStatusAsSuccess(EMAIL, CODE, DURATION);
        storage.isMarkedVerificationStatusWith(EMAIL, VerificationStatus.SUCCESS);
    }
}
