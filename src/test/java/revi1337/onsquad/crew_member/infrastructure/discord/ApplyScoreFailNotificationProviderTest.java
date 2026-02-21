package revi1337.onsquad.crew_member.infrastructure.discord;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage;
import revi1337.onsquad.infrastructure.network.discord.DiscordNotificationClient;

@ExtendWith(MockitoExtension.class)
class ApplyScoreFailNotificationProviderTest {

    @Mock
    private DiscordNotificationClient notificationClient;

    @Mock
    private OnsquadProperties onsquadProperties;

    @InjectMocks
    private ApplyScoreFailNotificationProvider provider;

    @Test
    @DisplayName("재시도 횟수 초과 시 디스코드 알림 메시지를 생성하여 전송한다")
    void sendExceedRetryAlert() {
        Long crewId = 1L;
        Long memberId = 2L;
        CrewActivity activity = CrewActivity.SQUAD_CREATE;
        when(onsquadProperties.getIdentifier()).thenReturn("local-test");
        ArgumentCaptor<DiscordMessage> captor = ArgumentCaptor.forClass(DiscordMessage.class);

        provider.sendExceedRetryAlert(crewId, memberId, activity);

        assertSoftly(softly -> {
            verify(notificationClient, times(1)).sendNotification(captor.capture());

            DiscordMessage sentMessage = captor.getValue();
            softly.assertThat(sentMessage.getUsername()).isEqualTo("OnSquad Crew Leaderboard");
            softly.assertThat(sentMessage.getContent()).contains("Max Retries Exceeded");
            softly.assertThat(sentMessage.getContent()).contains("**Target Crew ID:** `1`");
            softly.assertThat(sentMessage.getContent()).contains("**Target Member ID:** `2`");

            softly.assertThat(sentMessage.getEmbeds()).hasSize(1);
            softly.assertThat(sentMessage.getEmbeds().get(0).getTitle()).isEqualTo("Critical: Activity Score Lost");
            softly.assertThat(sentMessage.getEmbeds().get(0).getDescription()).contains(activity.name());
        });
    }
}
