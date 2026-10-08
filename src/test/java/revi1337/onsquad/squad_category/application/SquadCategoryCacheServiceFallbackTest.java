package revi1337.onsquad.squad_category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.container.UnreachableRedis;
import revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;

class SquadCategoryCacheServiceFallbackTest {
    private static final String CIRCUIT_BREAKER_NAME = "redisCacheCircuitBreaker";
    private static final int MINIMUM_NUMBER_OF_CALLS = 4;

    private final Logger serviceLogger = (Logger) LoggerFactory.getLogger(SquadCategoryCacheService.class);
    private final SquadCategoryAccessor squadCategoryAccessor = mock(SquadCategoryAccessor.class);
    private final SquadCategories mysqlCategories = new SquadCategories(List.of(
            new SimpleSquadCategory(1L, CategoryType.ACTIVITY),
            new SimpleSquadCategory(2L, CategoryType.BADMINTON)
    ));

    private ListAppender<ILoggingEvent> appender;
    private StringRedisTemplate brokenRedisTemplate;
    private CircuitBreakerRegistry registry;
    private CircuitBreaker circuitBreaker;
    private SquadCategoryCacheService cacheService;

    @BeforeEach
    void setUp() {
        appender = new ListAppender<>();
        appender.start();
        serviceLogger.addAppender(appender);

        registry = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                .slidingWindowSize(MINIMUM_NUMBER_OF_CALLS)
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMinutes(5))
                .recordExceptions(DataAccessException.class)
                .build());
        circuitBreaker = registry.circuitBreaker(CIRCUIT_BREAKER_NAME);

        brokenRedisTemplate = spy(UnreachableRedis.stringRedisTemplate());
        cacheService = new SquadCategoryCacheService(brokenRedisTemplate, new ObjectMapper(), squadCategoryAccessor, registry);
        when(squadCategoryAccessor.fetchCategoriesBySquadIdIn(List.of(1L, 2L))).thenReturn(mysqlCategories);
    }

    @AfterEach
    void tearDown() {
        serviceLogger.detachAppender(appender);
    }

    @Nested
    @DisplayName("서킷 CLOSED 상태에서 Redis 가 불가능한 경우")
    class WhenRedisIsDown {
        @Test
        @DisplayName("조회는 예외를 던지지 않고 MySQL 결과를 반환한다")
        void fallsBackToMysql() {
            SquadCategories result = cacheService.getCategoriesBySquadIdIn(List.of(1L, 2L));

            assertThat(result.values()).containsExactlyElementsOf(mysqlCategories.values());
            verify(squadCategoryAccessor, times(1)).fetchCategoriesBySquadIdIn(List.of(1L, 2L));
        }

        @Test
        @DisplayName("조회와 적재 실패는 각각 ERROR 레벨로 예외 타입과 스택트레이스를 남긴다")
        void logsGetAndPutFailures() {
            cacheService.getCategoriesBySquadIdIn(List.of(1L, 2L));

            List<ILoggingEvent> errors = appender.list.stream().filter(event -> event.getLevel() == Level.ERROR).toList();
            assertThat(errors).hasSize(2);
            assertThat(errors.get(0).getFormattedMessage()).contains("get", "RedisConnectionFailureException");
            assertThat(errors.get(1).getFormattedMessage()).contains("put", "RedisConnectionFailureException");
            assertThat(errors).allSatisfy(event -> {
                assertThat(event.getThrowableProxy()).isNotNull();
                assertThat(event.getThrowableProxy().getClassName()).contains("RedisConnectionFailureException");
            });
        }

        @Test
        @DisplayName("Redis 실패는 서킷브레이커에 실패로 기록된다")
        void recordsRedisFailures() {
            cacheService.getCategoriesBySquadIdIn(List.of(1L, 2L));

            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(2);
            assertThat(circuitBreaker.getState()).isEqualTo(State.CLOSED);
        }

        @Test
        @DisplayName("삭제는 예외를 던지지 않고 ERROR 레벨로 예외 타입과 스택트레이스를 남긴다")
        void evictDoesNotThrow() {
            assertThatCode(() -> cacheService.evictSquadCategories(List.of(1L, 2L))).doesNotThrowAnyException();

            ILoggingEvent errorEvent = appender.list.stream()
                    .filter(event -> event.getLevel() == Level.ERROR)
                    .findFirst()
                    .orElseThrow();
            assertThat(errorEvent.getFormattedMessage()).contains("evict", "RedisConnectionFailureException");
            assertThat(errorEvent.getThrowableProxy()).isNotNull();
        }

        @Test
        @DisplayName("조회할 스쿼드가 없으면 MySQL 도 호출하지 않는다")
        void emptySquadIds() {
            SquadCategories result = cacheService.getCategoriesBySquadIdIn(List.of());

            assertThat(result.values()).isEmpty();
            verify(squadCategoryAccessor, never()).fetchCategoriesBySquadIdIn(anyList());
        }
    }

    @Nested
    @DisplayName("서킷 OPEN 상태")
    class WhenCircuitIsOpen {
        @BeforeEach
        void openCircuit() {
            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS && circuitBreaker.getState() != State.OPEN; i++) {
                cacheService.getCategoriesBySquadIdIn(List.of(1L, 2L));
            }
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
            appender.list.clear();
            clearInvocations(brokenRedisTemplate, squadCategoryAccessor);
        }

        @Test
        @DisplayName("Redis 를 건드리지 않고 MySQL 결과를 반환한다")
        void doesNotTouchRedis() {
            SquadCategories result = cacheService.getCategoriesBySquadIdIn(List.of(1L, 2L));
            cacheService.evictSquadCategories(List.of(1L, 2L));

            assertThat(result.values()).containsExactlyElementsOf(mysqlCategories.values());
            verifyNoInteractions(brokenRedisTemplate);
            verify(squadCategoryAccessor, times(1)).fetchCategoriesBySquadIdIn(List.of(1L, 2L));
        }

        @Test
        @DisplayName("차단은 WARN 레벨로 스택트레이스 없이 남긴다")
        void logsWarnWithoutStackTrace() {
            cacheService.getCategoriesBySquadIdIn(List.of(1L, 2L));

            assertThat(appender.list).hasSize(2);
            assertThat(appender.list).allSatisfy(event -> {
                assertThat(event.getLevel()).isEqualTo(Level.WARN);
                assertThat(event.getFormattedMessage()).contains("차단", CIRCUIT_BREAKER_NAME);
                assertThat(event.getThrowableProxy()).isNull();
            });
        }
    }

    @Nested
    @DisplayName("DB 오류")
    class WhenDatabaseFails {
        @Test
        @DisplayName("MySQL 오류는 그대로 전파되고 서킷브레이커에 Redis 실패로 기록되지 않는다")
        void isNotCountedAsRedisFailure() {
            StringRedisTemplate healthyRedisTemplate = mock(StringRedisTemplate.class);
            @SuppressWarnings("unchecked")
            ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
            when(healthyRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(anyList())).thenReturn(Collections.singletonList(null));
            when(squadCategoryAccessor.fetchCategoriesBySquadIdIn(List.of(1L)))
                    .thenThrow(new DataRetrievalFailureException("mysql down"));
            SquadCategoryCacheService service = new SquadCategoryCacheService(healthyRedisTemplate, new ObjectMapper(), squadCategoryAccessor, registry);

            assertThatThrownBy(() -> service.getCategoriesBySquadIdIn(List.of(1L)))
                    .isInstanceOf(DataRetrievalFailureException.class);

            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
            assertThat(circuitBreaker.getState()).isEqualTo(State.CLOSED);
        }
    }
}
