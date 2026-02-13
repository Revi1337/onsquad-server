package revi1337.onsquad.crew_member.application.scheduler;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardUpdateService;
import revi1337.onsquad.crew_member.domain.model.CrewLeaderboards;
import revi1337.onsquad.infrastructure.storage.redis.RedisLockExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewLeaderboardRefreshScheduler {

    private static final String LOCK_KEY = "refresh-sch-lock";

    private final RedisLockExecutor redisLockExecutor;
    private final CrewLeaderboardManager leaderboardManager;
    private final CrewLeaderboardUpdateService leaderboardUpdateService;

    @Scheduled(cron = "${onsquad.api.crew-leaderboard.schedule.expression}")
    public void refreshLeaderboards() {
        redisLockExecutor.executeIfAcquired(LOCK_KEY, Duration.ofHours(1), () -> {
            try {
                log.info("[Leaderboard-Scheduler] Job initiated. Fetching candidates from Redis...");
                CrewLeaderboards leaderboards = leaderboardManager.getAllLeaderboards(CrewLeaderboardManager.RANKING_OVER_FETCH_SIZE);
                leaderboardUpdateService.updateLeaderboards(leaderboards);
                leaderboardManager.removeAllLeaderboards();
                log.info("[Leaderboard-Scheduler] Job completed. Leaderboard has been refreshed and Redis cleared.");
            } catch (Exception e) {
                log.error("[Leaderboard-Scheduler] Job failed due to unexpected error.", e);
            }
        });
    }
}
