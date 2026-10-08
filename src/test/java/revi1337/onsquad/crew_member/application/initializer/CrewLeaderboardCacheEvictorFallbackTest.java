package revi1337.onsquad.crew_member.application.initializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import revi1337.onsquad.common.container.UnreachableRedis;

class CrewLeaderboardCacheEvictorFallbackTest {

    private final Logger evictorLogger = (Logger) LoggerFactory.getLogger(CrewLeaderboardCacheEvictor.class);
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        appender = new ListAppender<>();
        appender.start();
        evictorLogger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        evictorLogger.detachAppender(appender);
    }

    @Test
    @DisplayName("Redis 연결이 불가능해도 구동 완료 이벤트 처리는 예외를 던지지 않는다")
    void doesNotThrow_whenRedisIsUnreachable() {
        CrewLeaderboardCacheEvictor evictor = new CrewLeaderboardCacheEvictor(UnreachableRedis.stringRedisTemplate());

        assertThatCode(evictor::onApplicationEvent).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Redis 장애는 ERROR 레벨로 예외 타입을 남기고 스택트레이스를 포함한다")
    void logsErrorWithExceptionTypeAndStackTrace_whenRedisIsUnreachable() {
        CrewLeaderboardCacheEvictor evictor = new CrewLeaderboardCacheEvictor(UnreachableRedis.stringRedisTemplate());

        evictor.onApplicationEvent();

        ILoggingEvent errorEvent = appender.list.stream()
                .filter(event -> event.getLevel() == Level.ERROR)
                .findFirst()
                .orElseThrow();
        assertThat(errorEvent.getFormattedMessage()).contains("RedisConnectionFailureException");
        assertThat(errorEvent.getThrowableProxy()).isNotNull();
        assertThat(errorEvent.getThrowableProxy().getClassName()).contains("RedisConnectionFailureException");
    }
}
