package revi1337.onsquad.crew_member.infrastructure.discord;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage.Embed;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage.Embed.Footer;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage.Embed.Thumbnail;
import revi1337.onsquad.infrastructure.network.discord.DiscordNotificationClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaderboardRefreshFailNotificationProvider {

    private static final String NOTIFICATION_PROVIDER_NAME = "OnSquad Crew Leaderboard Update Scheduler";
    private static final String NOTIFICATION_AVATAR_URL = "https://res.cloudinary.com/eightcruz/image/upload/c_lfill,h_120,w_120/perbhzmfdr5mecxo8w3y";
    private static final String SERVICE_NAME = "OnSquad Leaderboard Update Service";
    private static final String SERVICE_ICON_URL = "https://img.icons8.com/color/512/redis.png";

    private final DiscordNotificationClient leaderboardDiscordNotificationClient;
    private final OnsquadProperties onsquadProperties;

    public void sendLeaderboardUpdateFailAlert(LocalDateTime from, LocalDateTime to) {
        DiscordMessage message = createDiscordMessage(from, to);
        leaderboardDiscordNotificationClient.sendNotification(message);
    }

    private DiscordMessage createDiscordMessage(LocalDateTime from, LocalDateTime to) {
        String content = MessageFormat.format("""
                ⚠️ **Leaderboard Update: Process Failed**

                **Cause:** An unexpected error occurred during the scheduled leaderboard update.
                **Action Required:** Manual ranking synchronization or system status check (DB) is required.

                **Target Period:** `{0}` ~ `{1}`
                **Environment Identifier:** `{2}`
                """, from, to, onsquadProperties.getIdentifier()).translateEscapes();

        return DiscordMessage.builder()
                .username(NOTIFICATION_PROVIDER_NAME)
                .avatarUrl(NOTIFICATION_AVATAR_URL)
                .threadName(String.format("[%s] Leaderboard Update Failure (Instance: %s)", LocalDate.now(), onsquadProperties.getIdentifier()))
                .content(content)
                .embeds(buildEmbeds(from, to))
                .build();
    }

    private List<Embed> buildEmbeds(LocalDateTime from, LocalDateTime to) {
        String title = "Critical: Leaderboard Refresh Aborted";
        String description = MessageFormat.format("""
                The scheduled task failed to aggregate and reflect activity scores for the period `{0}` ~ `{1}`.
                Because the refresh runs within a single transaction, the previous `crew_ranker` table remains intact.
                Please check the application logs for the full stack trace and manually trigger the refresh if necessary.
                """, from, to).translateEscapes();

        return List.of(Embed.builder()
                .color(Embed.COLOR_RED)
                .title(title)
                .description(description)
                .thumbnail(new Thumbnail(SERVICE_ICON_URL))
                .footer(new Footer(SERVICE_NAME, SERVICE_ICON_URL))
                .build());
    }
}
