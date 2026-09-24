package revi1337.onsquad.crew_member.application.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardUpdateService;
import revi1337.onsquad.crew_member.config.CrewLeaderboardProperties;
import revi1337.onsquad.crew_member.infrastructure.discord.LeaderboardRefreshFailNotificationProvider;
import revi1337.onsquad.infrastructure.storage.redis.RedisLockExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewLeaderboardUpdateScheduler {

    private static final String LOCK_KEY = "leaderboard-sch-lock";

    private final RedisLockExecutor redisLockExecutor;
    private final CrewLeaderboardUpdateService leaderboardUpdateService;
    private final CrewLeaderboardProperties leaderboardProperties;
    private final LeaderboardRefreshFailNotificationProvider notificationProvider;

    @Scheduled(cron = "${onsquad.api.crew-leaderboard.schedule.expression}")
    public void updateLeaderboards() {
        redisLockExecutor.executeIfAcquired(LOCK_KEY, Duration.ofMinutes(5), () -> {
            LocalDateTime to = LocalDate.now().minusDays(1).atStartOfDay();
            LocalDateTime from = to.minusDays(leaderboardProperties.during().toDays());

            log.info("[Leaderboard-Scheduler] Job initiated. Aggregating activities between {} and {}.", from, to);
            try {
                leaderboardUpdateService.refreshLeaderboards(from, to, leaderboardProperties.rankLimit());
                log.info("[Leaderboard-Scheduler] Job completed successfully.");
            } catch (Exception exception) {
                log.error("[Leaderboard-Scheduler] Job failed during leaderboard refresh. Target period: {} ~ {}.", from, to, exception);
                notificationProvider.sendLeaderboardUpdateFailAlert(from, to);
            }
        });
    }
}
