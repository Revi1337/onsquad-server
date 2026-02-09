package revi1337.onsquad.crew_member.application.leaderboard;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("활동 점수 반영 시 예외가 발생하지 않으면 정상적으로 점수를 업데이트하고 알림을 보내지 않는다.")
    void test1() {
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
    void test2() {
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
}
