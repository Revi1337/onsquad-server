package revi1337.onsquad.crew.infrastructure.discord;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.crew.application.RetryableCrewRankingService;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage.Embed;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage.Embed.Footer;
import revi1337.onsquad.infrastructure.network.discord.DiscordMessage.Embed.Thumbnail;
import revi1337.onsquad.infrastructure.network.discord.DiscordNotificationClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyScoreFailNotificationProvider {

    private static final String NOTIFICATION_PROVIDER_NAME = "OnSquad Crew Leaderboard";
    private static final String NOTIFICATION_AVATAR_URL = "https://res.cloudinary.com/eightcruz/image/upload/c_lfill,h_120,w_120/perbhzmfdr5mecxo8w3y";
    private static final String SERVICE_NAME = "OnSquad Leaderboard Service";
    private static final String SERVICE_ICON_URL = "https://img.icons8.com/color/512/redis.png";

    private final DiscordNotificationClient applyScoreDiscordNotificationClient;
    private final OnsquadProperties onsquadProperties;

    public void sendExceedRetryAlert(Long crewId, Long memberId, CrewActivity activity) {
        DiscordMessage message = createDiscordMessage(crewId, memberId, activity);
        applyScoreDiscordNotificationClient.sendNotification(message);
    }

    private DiscordMessage createDiscordMessage(Long crewId, Long memberId, CrewActivity activity) {
        String content = MessageFormat.format("""
                ⚠️ **Ranking Update: Max Retries Exceeded**
                
                **Cause:** Failed to apply activity score after `{0}` attempts.
                **Action Required:** Manual score adjustment or data integrity check is required for this user.
                
                **Target Crew ID:** `{1}`
                **Target Member ID:** `{2}`
                **Activity Type:** `{3}`
                """, RetryableCrewRankingService.MAX_RETRY_COUNT, crewId, memberId, activity.name()).translateEscapes();

        return DiscordMessage.builder()
                .username(NOTIFICATION_PROVIDER_NAME)
                .avatarUrl(NOTIFICATION_AVATAR_URL)
                .threadName(String.format("[%s] Score Update Failure (Instance: %s)", LocalDate.now(), onsquadProperties.getIdentifier()))
                .content(content)
                .embeds(buildEmbeds(crewId, activity))
                .build();
    }

    private List<Embed> buildEmbeds(Long crewId, CrewActivity activity) {
        String title = "Critical: Activity Score Lost";
        String description = MessageFormat.format("""
                The system failed to update the leaderboard for **Crew ID: {0}**.
                Due to persistent Redis/Network issues, the score for **{1}** (+{2} pts) could not be applied.
                Please refer to the application logs for the full trace and manually reflect this change to maintain ranking fairness.
                """, crewId, activity.name(), activity.getScore()).translateEscapes();

        return List.of(Embed.builder()
                .color(Embed.COLOR_RED)
                .title(title)
                .description(description)
                .thumbnail(new Thumbnail(SERVICE_ICON_URL))
                .footer(new Footer(SERVICE_NAME, SERVICE_ICON_URL))
                .build());
    }
}
