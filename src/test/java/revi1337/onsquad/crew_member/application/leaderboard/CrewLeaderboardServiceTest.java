package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.infrastructure.discord.ApplyScoreFailNotificationProvider;

class CrewLeaderboardServiceTest extends ApplicationLayerTestSupport {

    @MockBean
    private CrewLeaderboardManager delegate;

    @MockBean
    private ApplyScoreFailNotificationProvider notificationProvider;

    @SpyBean
    private CrewLeaderboardService leaderboardService;

    private final Logger serviceLogger = (Logger) LoggerFactory.getLogger(CrewLeaderboardService.class);
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        appender = new ListAppender<>();
        appender.start();
        serviceLogger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        serviceLogger.detachAppender(appender);
    }

    @Test
    @DisplayName("활동 점수 반영 시 예외가 발생하지 않으면 정상적으로 점수를 업데이트하고 알림을 보내지 않는다.")
    void appliesScoreWithoutAlert_whenNoExceptionOccurs() {
        Long crewId = 1L;
        Long memerId = 2L;
        Instant applyAt = LocalDateTime.of(2026, 1, 4, 0, 0, 0).toInstant(ZoneOffset.UTC);
        CrewActivity crewActivity = CrewActivity.SQUAD_CREATE;
        doNothing().when(delegate).applyActivity(crewId, memerId, applyAt, crewActivity);

        leaderboardService.applyActivity(crewId, memerId, applyAt, crewActivity);

        verify(delegate).applyActivity(crewId, memerId, applyAt, crewActivity);
        verify(notificationProvider, times(0)).sendExceedRetryAlert(crewId, memerId, crewActivity);
    }

    @Test
    @DisplayName("예외가 지속적으로 발생하면 최대 3번 재시도한 후, @Recover 메서드를 통해 관리자에게 알림을 보낸다.")
    void retriesThreeTimesThenSendsAlert_whenExceptionPersists() {
        Long crewId = 1L;
        Long memerId = 2L;
        Instant applyAt = LocalDateTime.of(2026, 1, 4, 0, 0, 0).toInstant(ZoneOffset.UTC);
        CrewActivity crewActivity = CrewActivity.SQUAD_CREATE;
        willThrow(RuntimeException.class)
                .given(delegate).applyActivity(crewId, memerId, applyAt, crewActivity);

        leaderboardService.applyActivity(crewId, memerId, applyAt, crewActivity);

        verify(delegate, times(3)).applyActivity(crewId, memerId, applyAt, crewActivity);
        verify(notificationProvider, times(1)).sendExceedRetryAlert(crewId, memerId, crewActivity);
    }

    @Test
    @DisplayName("재시도를 모두 소진하면 시도 횟수와 무관하게 크루, 회원, 활동 정보와 예외를 담은 에러 로그를 정확히 한 번만 남긴다.")
    void logsExactlyOnceWithContext_whenRetriesExhausted() {
        Long crewId = 1L;
        Long memberId = 2L;
        Instant applyAt = LocalDateTime.of(2026, 1, 4, 0, 0, 0).toInstant(ZoneOffset.UTC);
        CrewActivity crewActivity = CrewActivity.SQUAD_CREATE;
        willThrow(new IllegalStateException("redis down"))
                .given(delegate).applyActivity(crewId, memberId, applyAt, crewActivity);

        leaderboardService.applyActivity(crewId, memberId, applyAt, crewActivity);

        verify(delegate, times(3)).applyActivity(crewId, memberId, applyAt, crewActivity);
        assertThat(appender.list).hasSize(1);
        ILoggingEvent event = appender.list.get(0);
        assertThat(event.getLevel()).isEqualTo(Level.ERROR);
        assertThat(event.getFormattedMessage()).contains("crewId=1", "memberId=2", "activity=SQUAD_CREATE", "IllegalStateException", "redis down");
        assertThat(event.getThrowableProxy()).isNotNull();
        assertThat(event.getThrowableProxy().getClassName()).isEqualTo(IllegalStateException.class.getName());
    }

    @Test
    @DisplayName("재시도 중 성공하면 실패한 시도에 대해서도 로그를 남기지 않고 알림도 보내지 않는다.")
    void logsNothing_whenSucceedsOnRetry() {
        Long crewId = 1L;
        Long memberId = 2L;
        Instant applyAt = LocalDateTime.of(2026, 1, 4, 0, 0, 0).toInstant(ZoneOffset.UTC);
        CrewActivity crewActivity = CrewActivity.SQUAD_CREATE;
        willThrow(new IllegalStateException("redis down"))
                .willDoNothing()
                .given(delegate).applyActivity(crewId, memberId, applyAt, crewActivity);

        leaderboardService.applyActivity(crewId, memberId, applyAt, crewActivity);

        verify(delegate, times(2)).applyActivity(crewId, memberId, applyAt, crewActivity);
        verify(notificationProvider, never()).sendExceedRetryAlert(crewId, memberId, crewActivity);
        assertThat(appender.list).isEmpty();
    }

    @Test
    @DisplayName("처음부터 성공하면 로그를 남기지 않는다.")
    void logsNothing_whenSucceedsImmediately() {
        Long crewId = 1L;
        Long memberId = 2L;
        Instant applyAt = LocalDateTime.of(2026, 1, 4, 0, 0, 0).toInstant(ZoneOffset.UTC);
        CrewActivity crewActivity = CrewActivity.SQUAD_CREATE;
        doNothing().when(delegate).applyActivity(crewId, memberId, applyAt, crewActivity);

        leaderboardService.applyActivity(crewId, memberId, applyAt, crewActivity);

        assertThat(appender.list).isEmpty();
    }
}
