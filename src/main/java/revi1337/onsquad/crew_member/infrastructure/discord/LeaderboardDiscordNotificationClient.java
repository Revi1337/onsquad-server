package revi1337.onsquad.crew_member.infrastructure.discord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.network.discord.AbstractDiscordNotificationClient;

@Component
public class LeaderboardDiscordNotificationClient extends AbstractDiscordNotificationClient {

    private final String webhookEndpoint;

    public LeaderboardDiscordNotificationClient(@Value("${onsquad.discord.webhook.alert.leaderboard-scheduler}") String webhookEndpoint) {
        this.webhookEndpoint = webhookEndpoint;
    }

    @Override
    protected String getWebhookEndpoint() {
        return webhookEndpoint;
    }
}
