package revi1337.onsquad.auth.token.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import revi1337.onsquad.auth.token.application.RefreshTokenStorage;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;

class ResilientRefreshTokenStorageTest {

    private static final String CIRCUIT_BREAKER_NAME = "redisAuthCircuitBreaker";
    private static final int MINIMUM_NUMBER_OF_CALLS = 4;
    private static final Long MEMBER_ID = 1L;
    private static final Duration DURATION = Duration.ofDays(7);
    private static final long REDIS_SAVE_RESULT = 111L;
    private static final long RDB_SAVE_RESULT = 777L;

    private final Logger storageLogger = (Logger) LoggerFactory.getLogger(ResilientRefreshTokenStorage.class);
    private final RefreshTokenStorage redisStorage = mock(RefreshTokenStorage.class);
    private final RefreshTokenStorage rdbStorage = mock(RefreshTokenStorage.class);
    private final RefreshToken refreshToken = new RefreshToken(MEMBER_ID, "refresh-token-value", new Date(System.currentTimeMillis() + DURATION.toMillis()));

    private ListAppender<ILoggingEvent> appender;
    private CircuitBreaker circuitBreaker;
    private ResilientRefreshTokenStorage storage;

    static Stream<RuntimeException> redisFailures() {
        return Stream.of(
                new RedisConnectionFailureException("redis down"),
                new QueryTimeoutException("redis timeout")
        );
    }

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
        storage = new ResilientRefreshTokenStorage(redisStorage, rdbStorage, registry);
    }

    @AfterEach
    void tearDown() {
        storageLogger.detachAppender(appender);
    }

    private void openCircuit() {
        when(redisStorage.findTokenBy(anyLong())).thenThrow(new RedisConnectionFailureException("redis down"));
        when(rdbStorage.findTokenBy(anyLong())).thenReturn(Optional.empty());
        for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS && circuitBreaker.getState() != State.OPEN; i++) {
            storage.findTokenBy(MEMBER_ID);
        }
        assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
        appender.list.clear();
        clearInvocations(redisStorage, rdbStorage);
    }

    @Nested
    @DisplayName("저장")
    class Save {

        @Test
        @DisplayName("둘 다 성공하면 두 저장소 모두에 저장하고 Redis 의 반환값을 반환한다")
        void savesToBoth() {
            when(redisStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenReturn(REDIS_SAVE_RESULT);
            when(rdbStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenReturn(RDB_SAVE_RESULT);

            long result = storage.saveToken(MEMBER_ID, refreshToken, DURATION);

            assertThat(result).isEqualTo(REDIS_SAVE_RESULT);
            verify(redisStorage).saveToken(MEMBER_ID, refreshToken, DURATION);
            verify(rdbStorage).saveToken(MEMBER_ID, refreshToken, DURATION);
            assertThat(appender.list).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("revi1337.onsquad.auth.token.infrastructure.persistence.ResilientRefreshTokenStorageTest#redisFailures")
        @DisplayName("Redis 가 실패하면 RDB 에만 저장되고 예외 없이 RDB 의 반환값을 반환하며 ERROR 로그를 남긴다")
        void savesOnlyToRdbWhenRedisFails(RuntimeException redisFailure) {
            when(redisStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenThrow(redisFailure);
            when(rdbStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenReturn(RDB_SAVE_RESULT);

            long result = storage.saveToken(MEMBER_ID, refreshToken, DURATION);

            assertThat(result).isEqualTo(RDB_SAVE_RESULT);
            verify(rdbStorage).saveToken(MEMBER_ID, refreshToken, DURATION);
            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getFormattedMessage()).contains(redisFailure.getClass().getSimpleName(), redisFailure.getMessage());
            assertThat(event.getThrowableProxy()).isNotNull();
            assertThat(event.getThrowableProxy().getClassName()).isEqualTo(redisFailure.getClass().getName());
        }

        @Test
        @DisplayName("RDB 가 실패하면 Redis 에만 저장되고 예외 없이 Redis 의 반환값을 반환하며 ERROR 로그를 남긴다")
        void savesOnlyToRedisWhenRdbFails() {
            when(redisStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenReturn(REDIS_SAVE_RESULT);
            when(rdbStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenThrow(new DataRetrievalFailureException("mysql down"));

            long result = storage.saveToken(MEMBER_ID, refreshToken, DURATION);

            assertThat(result).isEqualTo(REDIS_SAVE_RESULT);
            verify(redisStorage).saveToken(MEMBER_ID, refreshToken, DURATION);
            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getFormattedMessage()).contains("RDB", "DataRetrievalFailureException", "mysql down");
            assertThat(event.getThrowableProxy()).isNotNull();
        }

        @Test
        @DisplayName("둘 다 실패하면 RDB 예외를 던지고 Redis 예외는 suppressed 로 남긴다")
        void throwsWhenBothFail() {
            RedisConnectionFailureException redisFailure = new RedisConnectionFailureException("redis down");
            DataRetrievalFailureException rdbFailure = new DataRetrievalFailureException("mysql down");
            when(redisStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenThrow(redisFailure);
            when(rdbStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenThrow(rdbFailure);

            assertThatThrownBy(() -> storage.saveToken(MEMBER_ID, refreshToken, DURATION))
                    .isSameAs(rdbFailure)
                    .hasSuppressedException(redisFailure);
        }

        @Test
        @DisplayName("서킷이 OPEN 이면 Redis 는 호출하지 않고 RDB 에만 저장하며 WARN 로그를 스택트레이스 없이 남긴다")
        void skipsRedisWhenCircuitIsOpen() {
            openCircuit();
            when(rdbStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenReturn(RDB_SAVE_RESULT);

            long result = storage.saveToken(MEMBER_ID, refreshToken, DURATION);

            assertThat(result).isEqualTo(RDB_SAVE_RESULT);
            verifyNoInteractions(redisStorage);
            verify(rdbStorage).saveToken(MEMBER_ID, refreshToken, DURATION);
            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.WARN);
            assertThat(event.getFormattedMessage()).contains("차단", CIRCUIT_BREAKER_NAME);
            assertThat(event.getThrowableProxy()).isNull();
        }

        @Test
        @DisplayName("서킷이 OPEN 인데 RDB 도 실패하면 RDB 예외를 던지고 차단 예외를 suppressed 로 남긴다")
        void throwsWhenCircuitIsOpenAndRdbFails() {
            openCircuit();
            DataRetrievalFailureException rdbFailure = new DataRetrievalFailureException("mysql down");
            when(rdbStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenThrow(rdbFailure);

            assertThatThrownBy(() -> storage.saveToken(MEMBER_ID, refreshToken, DURATION))
                    .isSameAs(rdbFailure)
                    .satisfies(thrown -> assertThat(thrown.getSuppressed()).hasSize(1).allSatisfy(suppressed -> assertThat(suppressed).isInstanceOf(CallNotPermittedException.class)));
            verifyNoInteractions(redisStorage);
        }
    }

    @Nested
    @DisplayName("조회")
    class Find {

        @Test
        @DisplayName("Redis 에 값이 있으면 그대로 반환하고 RDB 는 호출하지 않는다")
        void returnsRedisValueWithoutTouchingRdb() {
            when(redisStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.of(refreshToken));

            Optional<RefreshToken> result = storage.findTokenBy(MEMBER_ID);

            assertThat(result).contains(refreshToken);
            verifyNoInteractions(rdbStorage);
            assertThat(appender.list).isEmpty();
        }

        @Test
        @DisplayName("Redis 가 비어 있으면 RDB 의 값을 반환한다")
        void fallsBackToRdbWhenRedisIsEmpty() {
            when(redisStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.empty());
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.of(refreshToken));

            Optional<RefreshToken> result = storage.findTokenBy(MEMBER_ID);

            assertThat(result).contains(refreshToken);
            verify(rdbStorage).findTokenBy(MEMBER_ID);
            assertThat(appender.list).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("revi1337.onsquad.auth.token.infrastructure.persistence.ResilientRefreshTokenStorageTest#redisFailures")
        @DisplayName("Redis 가 실패하면 RDB 의 값을 반환하고 ERROR 로그를 남긴다")
        void fallsBackToRdbWhenRedisFails(RuntimeException redisFailure) {
            when(redisStorage.findTokenBy(MEMBER_ID)).thenThrow(redisFailure);
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.of(refreshToken));

            Optional<RefreshToken> result = storage.findTokenBy(MEMBER_ID);

            assertThat(result).contains(refreshToken);
            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getThrowableProxy()).isNotNull();
            assertThat(event.getThrowableProxy().getClassName()).isEqualTo(redisFailure.getClass().getName());
        }

        @Test
        @DisplayName("Redis 가 비어 있는데 RDB 가 예외를 던지면 빈 Optional 을 반환하고 ERROR 로그를 남긴다")
        void returnsEmptyWhenRedisIsEmptyAndRdbFails() {
            when(redisStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.empty());
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenThrow(new DataRetrievalFailureException("table not found"));

            Optional<RefreshToken> result = storage.findTokenBy(MEMBER_ID);

            assertThat(result).isEmpty();
            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getFormattedMessage()).contains("RDB", "DataRetrievalFailureException", "table not found");
            assertThat(event.getThrowableProxy()).isNotNull();
        }

        @Test
        @DisplayName("Redis 도 실패하고 RDB 도 예외를 던지면 RDB 예외를 던지고 Redis 예외는 suppressed 로 남긴다")
        void throwsWhenBothFail() {
            RedisConnectionFailureException redisFailure = new RedisConnectionFailureException("redis down");
            DataRetrievalFailureException rdbFailure = new DataRetrievalFailureException("mysql down");
            when(redisStorage.findTokenBy(MEMBER_ID)).thenThrow(redisFailure);
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenThrow(rdbFailure);

            assertThatThrownBy(() -> storage.findTokenBy(MEMBER_ID))
                    .isSameAs(rdbFailure)
                    .hasSuppressedException(redisFailure);
        }

        @Test
        @DisplayName("서킷이 OPEN 이면 Redis 는 호출하지 않고 RDB 의 값을 반환하며 WARN 로그를 스택트레이스 없이 남긴다")
        void skipsRedisWhenCircuitIsOpen() {
            openCircuit();
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.of(refreshToken));

            Optional<RefreshToken> result = storage.findTokenBy(MEMBER_ID);

            assertThat(result).contains(refreshToken);
            verifyNoInteractions(redisStorage);
            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.WARN);
            assertThat(event.getThrowableProxy()).isNull();
        }
    }

    @Nested
    @DisplayName("단건 삭제")
    class DeleteToken {

        @Test
        @DisplayName("둘 다 성공하면 두 저장소 모두에서 삭제한다")
        void deletesFromBoth() {
            storage.deleteTokenBy(MEMBER_ID);

            verify(redisStorage).deleteTokenBy(MEMBER_ID);
            verify(rdbStorage).deleteTokenBy(MEMBER_ID);
            assertThat(appender.list).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("revi1337.onsquad.auth.token.infrastructure.persistence.ResilientRefreshTokenStorageTest#redisFailures")
        @DisplayName("Redis 가 실패해도 RDB 에서 삭제하고 예외 없이 ERROR 로그를 남긴다")
        void deletesFromRdbWhenRedisFails(RuntimeException redisFailure) {
            doThrow(redisFailure).when(redisStorage).deleteTokenBy(MEMBER_ID);

            storage.deleteTokenBy(MEMBER_ID);

            verify(rdbStorage).deleteTokenBy(MEMBER_ID);
            assertThat(appender.list).hasSize(1);
            assertThat(appender.list.get(0).getLevel()).isEqualTo(Level.ERROR);
            assertThat(appender.list.get(0).getThrowableProxy()).isNotNull();
        }

        @Test
        @DisplayName("RDB 가 실패해도 Redis 에서는 삭제되고 예외 없이 ERROR 로그를 남긴다")
        void deletesFromRedisWhenRdbFails() {
            doThrow(new DataRetrievalFailureException("mysql down")).when(rdbStorage).deleteTokenBy(MEMBER_ID);

            storage.deleteTokenBy(MEMBER_ID);

            verify(redisStorage).deleteTokenBy(MEMBER_ID);
            assertThat(appender.list).hasSize(1);
            assertThat(appender.list.get(0).getLevel()).isEqualTo(Level.ERROR);
            assertThat(appender.list.get(0).getFormattedMessage()).contains("RDB");
        }

        @Test
        @DisplayName("둘 다 실패하면 RDB 예외를 던지고 Redis 예외는 suppressed 로 남긴다")
        void throwsWhenBothFail() {
            RedisConnectionFailureException redisFailure = new RedisConnectionFailureException("redis down");
            DataRetrievalFailureException rdbFailure = new DataRetrievalFailureException("mysql down");
            doThrow(redisFailure).when(redisStorage).deleteTokenBy(MEMBER_ID);
            doThrow(rdbFailure).when(rdbStorage).deleteTokenBy(MEMBER_ID);

            assertThatThrownBy(() -> storage.deleteTokenBy(MEMBER_ID))
                    .isSameAs(rdbFailure)
                    .hasSuppressedException(redisFailure);
        }

        @Test
        @DisplayName("서킷이 OPEN 이면 Redis 는 호출하지 않고 RDB 에서만 삭제한다")
        void skipsRedisWhenCircuitIsOpen() {
            openCircuit();

            storage.deleteTokenBy(MEMBER_ID);

            verifyNoInteractions(redisStorage);
            verify(rdbStorage).deleteTokenBy(MEMBER_ID);
            assertThat(appender.list).hasSize(1);
            assertThat(appender.list.get(0).getLevel()).isEqualTo(Level.WARN);
        }
    }

    @Nested
    @DisplayName("전체 삭제")
    class DeleteAll {

        @Test
        @DisplayName("둘 다 성공하면 두 저장소 모두에서 삭제한다")
        void deletesFromBoth() {
            storage.deleteAll();

            verify(redisStorage).deleteAll();
            verify(rdbStorage).deleteAll();
            assertThat(appender.list).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("revi1337.onsquad.auth.token.infrastructure.persistence.ResilientRefreshTokenStorageTest#redisFailures")
        @DisplayName("Redis 가 실패해도 RDB 에서 삭제하고 예외 없이 ERROR 로그를 남긴다")
        void deletesFromRdbWhenRedisFails(RuntimeException redisFailure) {
            doThrow(redisFailure).when(redisStorage).deleteAll();

            storage.deleteAll();

            verify(rdbStorage).deleteAll();
            assertThat(appender.list).hasSize(1);
            assertThat(appender.list.get(0).getLevel()).isEqualTo(Level.ERROR);
            assertThat(appender.list.get(0).getThrowableProxy()).isNotNull();
        }

        @Test
        @DisplayName("RDB 가 실패해도 Redis 에서는 삭제되고 예외 없이 ERROR 로그를 남긴다")
        void deletesFromRedisWhenRdbFails() {
            doThrow(new DataRetrievalFailureException("mysql down")).when(rdbStorage).deleteAll();

            storage.deleteAll();

            verify(redisStorage).deleteAll();
            assertThat(appender.list).hasSize(1);
            assertThat(appender.list.get(0).getLevel()).isEqualTo(Level.ERROR);
            assertThat(appender.list.get(0).getFormattedMessage()).contains("RDB");
        }

        @Test
        @DisplayName("둘 다 실패하면 RDB 예외를 던지고 Redis 예외는 suppressed 로 남긴다")
        void throwsWhenBothFail() {
            RedisConnectionFailureException redisFailure = new RedisConnectionFailureException("redis down");
            DataRetrievalFailureException rdbFailure = new DataRetrievalFailureException("mysql down");
            doThrow(redisFailure).when(redisStorage).deleteAll();
            doThrow(rdbFailure).when(rdbStorage).deleteAll();

            assertThatThrownBy(() -> storage.deleteAll())
                    .isSameAs(rdbFailure)
                    .hasSuppressedException(redisFailure);
        }

        @Test
        @DisplayName("서킷이 OPEN 이면 Redis 는 호출하지 않고 RDB 에서만 삭제한다")
        void skipsRedisWhenCircuitIsOpen() {
            openCircuit();

            storage.deleteAll();

            verifyNoInteractions(redisStorage);
            verify(rdbStorage).deleteAll();
        }
    }

    @Nested
    @DisplayName("서킷브레이커 집계")
    class CircuitMetrics {

        @Test
        @DisplayName("Redis 실패는 서킷브레이커에 실패로 기록된다")
        void recordsRedisFailures() {
            when(redisStorage.findTokenBy(MEMBER_ID)).thenThrow(new RedisConnectionFailureException("redis down"));
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.empty());
            doThrow(new QueryTimeoutException("redis timeout")).when(redisStorage).deleteTokenBy(MEMBER_ID);

            storage.findTokenBy(MEMBER_ID);
            storage.deleteTokenBy(MEMBER_ID);

            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(2);
            assertThat(circuitBreaker.getState()).isEqualTo(State.CLOSED);
        }

        @Test
        @DisplayName("Redis 가 정상일 때 RDB 예외가 반복돼도 서킷브레이커는 실패로 집계하지 않고 CLOSED 를 유지한다")
        void rdbExceptionsAreNotRecordedWhenRedisSucceeds() {
            when(redisStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenReturn(REDIS_SAVE_RESULT);
            when(rdbStorage.saveToken(MEMBER_ID, refreshToken, DURATION)).thenThrow(new DataRetrievalFailureException("mysql down"));
            when(redisStorage.findTokenBy(MEMBER_ID)).thenReturn(Optional.empty());
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenThrow(new DataRetrievalFailureException("mysql down"));

            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
                storage.saveToken(MEMBER_ID, refreshToken, DURATION);
                storage.findTokenBy(MEMBER_ID);
            }

            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
            assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(MINIMUM_NUMBER_OF_CALLS);
            assertThat(circuitBreaker.getState()).isEqualTo(State.CLOSED);
            verify(rdbStorage, times(MINIMUM_NUMBER_OF_CALLS)).saveToken(MEMBER_ID, refreshToken, DURATION);
        }

        @Test
        @DisplayName("Redis 와 RDB 가 함께 실패해도 집계되는 것은 Redis 호출 1건뿐이다")
        void onlyRedisCallIsRecordedWhenBothFail() {
            when(redisStorage.findTokenBy(MEMBER_ID)).thenThrow(new RedisConnectionFailureException("redis down"));
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenThrow(new DataRetrievalFailureException("mysql down"));

            assertThatThrownBy(() -> storage.findTokenBy(MEMBER_ID)).isInstanceOf(DataRetrievalFailureException.class);

            assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls()).isEqualTo(1);
            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
        }

        @Test
        @DisplayName("서킷이 열린 상태에서 RDB 예외가 반복돼도 실패 집계와 상태는 변하지 않는다")
        void rdbExceptionsWhileOpenDoNotChangeMetrics() {
            openCircuit();
            int failedCallsWhenOpened = circuitBreaker.getMetrics().getNumberOfFailedCalls();
            when(rdbStorage.findTokenBy(MEMBER_ID)).thenThrow(new DataRetrievalFailureException("mysql down"));

            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
                assertThatThrownBy(() -> storage.findTokenBy(MEMBER_ID)).isInstanceOf(DataRetrievalFailureException.class);
            }

            verifyNoInteractions(redisStorage);
            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(failedCallsWhenOpened);
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
        }
    }
}
