package revi1337.onsquad.announce.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoInteractions;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.LoggerFactory;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.common.container.UnreachableRedis;

/**
 * Redis 연결이 불가능한 상황에서 {@link RedisAnnounceCacheEvictor} 가 예외를 전파하지 않고, 서킷브레이커가 OPEN 되면 Redis 를 더 이상 시도하지 않는지 검증한다.
 */
class RedisAnnounceCacheEvictorFallbackTest {

    private static final String CIRCUIT_BREAKER_NAME = "redisCacheCircuitBreaker";
    private static final int MINIMUM_NUMBER_OF_CALLS = 6;

    private final Logger evictorLogger = (Logger) LoggerFactory.getLogger(RedisAnnounceCacheEvictor.class);
    private ListAppender<ILoggingEvent> appender;
    private StringRedisTemplate brokenRedisTemplate;
    private CircuitBreaker circuitBreaker;
    private RedisAnnounceCacheEvictor evictor;

    @BeforeEach
    void setUp() {
        appender = new ListAppender<>();
        appender.start();
        evictorLogger.addAppender(appender);

        brokenRedisTemplate = spy(UnreachableRedis.stringRedisTemplate());
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                .slidingWindowSize(MINIMUM_NUMBER_OF_CALLS)
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                .failureRateThreshold(50)
                .waitDurationInOpenState(java.time.Duration.ofMinutes(5))
                .recordExceptions(DataAccessException.class)
                .build());
        circuitBreaker = registry.circuitBreaker(CIRCUIT_BREAKER_NAME);
        evictor = new RedisAnnounceCacheEvictor(new ConcurrentMapCacheManager(), brokenRedisTemplate, registry);
    }

    @AfterEach
    void tearDown() {
        evictorLogger.detachAppender(appender);
    }

    @Nested
    @DisplayName("서킷 CLOSED 상태에서 Redis 가 불가능한 경우")
    class WhenRedisIsDown {

        @Test
        @DisplayName("evictAnnounce 는 예외를 던지지 않는다")
        void evictAnnounce() {
            assertThatCode(() -> evictor.evictAnnounce(1L, 2L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("evictAnnounces(Long) 은 예외를 던지지 않는다")
        void evictAnnouncesByCrewId() {
            assertThatCode(() -> evictor.evictAnnounces(1L)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("evictAnnounces(List) 는 예외를 던지지 않는다")
        void evictAnnouncesByCrewIds() {
            assertThatCode(() -> evictor.evictAnnounces(List.of(1L, 2L))).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("evictAnnouncesByReferences 는 예외를 던지지 않는다")
        void evictAnnouncesByReferences() {
            assertThatCode(() -> evictor.evictAnnouncesByReferences(List.of(new AnnounceReference(1L, 2L)))).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("evictAnnounceLists 는 예외를 던지지 않는다")
        void evictAnnounceLists() {
            assertThatCode(() -> evictor.evictAnnounceLists(List.of(1L, 2L))).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패는 ERROR 레벨로 예외 타입을 남기고 스택트레이스를 포함한다")
        void logsErrorWithExceptionTypeAndStackTrace() {
            evictor.evictAnnounce(1L, 2L);

            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getFormattedMessage()).contains("RedisConnectionFailureException");
            assertThat(event.getThrowableProxy()).isNotNull();
            assertThat(event.getThrowableProxy().getClassName()).contains("RedisConnectionFailureException");
        }

        @Test
        @DisplayName("실패한 호출은 서킷브레이커에 실패로 기록된다")
        void recordsFailureOnCircuitBreaker() {
            evictor.evictAnnounce(1L, 2L);
            evictor.evictAnnounces(1L);

            assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(2);
            assertThat(circuitBreaker.getState()).isEqualTo(State.CLOSED);
        }
    }

    @Nested
    @DisplayName("서킷 OPEN 상태")
    class WhenCircuitIsOpen {

        @BeforeEach
        void openCircuit() {
            for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
                evictor.evictAnnounce(1L, 2L);
            }
            assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
            appender.list.clear();
            clearInvocations(brokenRedisTemplate);
        }

        @Test
        @DisplayName("5개 삭제 메서드 모두 예외를 던지지 않고 Redis 를 건드리지 않는다")
        void doesNotTouchRedis() {
            Executable[] evictions = {
                    () -> evictor.evictAnnounce(1L, 2L),
                    () -> evictor.evictAnnounces(1L),
                    () -> evictor.evictAnnounces(List.of(1L, 2L)),
                    () -> evictor.evictAnnouncesByReferences(List.of(new AnnounceReference(1L, 2L))),
                    () -> evictor.evictAnnounceLists(List.of(1L, 2L))
            };

            for (Executable eviction : evictions) {
                assertThatCode(eviction::execute).doesNotThrowAnyException();
            }

            verifyNoInteractions(brokenRedisTemplate);
        }

        @Test
        @DisplayName("차단은 WARN 레벨로 스택트레이스 없이 남긴다")
        void logsWarnWithoutStackTrace() {
            evictor.evictAnnounce(1L, 2L);

            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getLevel()).isEqualTo(Level.WARN);
            assertThat(event.getFormattedMessage()).contains("차단", CIRCUIT_BREAKER_NAME);
            assertThat(event.getThrowableProxy()).isNull();
        }
    }
}
