package revi1337.onsquad.crew_member.infrastructure.discord;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage;
import revi1337.onsquad.infrastructure.network.discord.DiscordNotificationClient;

@ExtendWith(MockitoExtension.class)
class LeaderboardRefreshFailNotificationProviderTest {

    @Mock
    private DiscordNotificationClient notificationClient;

    @Mock
    private OnsquadProperties onsquadProperties;

    @InjectMocks
    private LeaderboardRefreshFailNotificationProvider provider;

    @Test
    @DisplayName("리더보드 갱신 실패 시 집계 대상 기간이 포함된 알림을 전송한다")
    void sendLeaderboardUpdateFailAlert() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 5, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 12, 0, 0);
        when(onsquadProperties.getIdentifier()).thenReturn("prod-env");
        ArgumentCaptor<DiscordMessage> messageCaptor = ArgumentCaptor.forClass(DiscordMessage.class);

        provider.sendLeaderboardUpdateFailAlert(from, to);

        assertSoftly(softly -> {
            verify(notificationClient, times(1)).sendNotification(messageCaptor.capture());

            DiscordMessage sentMessage = messageCaptor.getValue();
            softly.assertThat(sentMessage.getUsername()).isEqualTo("OnSquad Crew Leaderboard Update Scheduler");
            softly.assertThat(sentMessage.getContent()).contains("Leaderboard Update: Process Failed");
            softly.assertThat(sentMessage.getContent()).contains(from.toString());
            softly.assertThat(sentMessage.getContent()).contains(to.toString());
            softly.assertThat(sentMessage.getContent()).contains("Environment Identifier");
            softly.assertThat(sentMessage.getContent()).contains("`prod-env`");

            softly.assertThat(sentMessage.getEmbeds().get(0).getTitle()).isEqualTo("Critical: Leaderboard Refresh Aborted");
        });
    }
}
