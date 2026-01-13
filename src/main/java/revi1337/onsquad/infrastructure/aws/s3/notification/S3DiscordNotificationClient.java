package revi1337.onsquad.infrastructure.aws.s3.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.network.discord.AbstractDiscordNotificationClient;

@Component
public class S3DiscordNotificationClient extends AbstractDiscordNotificationClient {

    private final String webhookEndpoint;

    public S3DiscordNotificationClient(@Value("${onsquad.discord.webhook.alert.aws}") String webhookEndpoint) {
        this.webhookEndpoint = webhookEndpoint;
    }

    @Override
    protected String getWebhookEndpoint() {
        return webhookEndpoint;
    }
}
