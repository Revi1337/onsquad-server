package revi1337.onsquad.crew_member.application.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardBackupManager;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardRebuildService;
import revi1337.onsquad.crew_member.config.CrewLeaderboardProperties;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;
import revi1337.onsquad.infrastructure.storage.redis.RedisLockExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewLeaderboardRefreshScheduler {

    private static final String LOCK_KEY = "refresh-sch-lock";

    private final RedisLockExecutor redisLockExecutor;
    private final CrewLeaderboardProperties crewLeaderboardProperties;
    private final CrewLeaderboardManager leaderboardManager;
    private final CrewLeaderboardRebuildService leaderboardRebuildService;
    private final CrewLeaderboardBackupManager leaderboardBackupManager;

    @Scheduled(cron = "${onsquad.api.crew-leaderboard.schedule.expression}")
    public void refreshRankedMembers() {
        redisLockExecutor.executeIfAcquired(LOCK_KEY, Duration.ofHours(1), () -> {
            log.info("[Leaderboard Refresh] Task started.");
            try {
                leaderboardBackupManager.backupCurrentTopRankers();
                List<CrewRankerDetail> currentRankedMembers = leaderboardManager.getAllLeaderboards(crewLeaderboardProperties.rankLimit());
                leaderboardRebuildService.renewTopRankers(currentRankedMembers);
                leaderboardManager.removeAllLeaderboards();
                log.info("[Leaderboard Refresh] Task completed successfully.");
            } catch (Exception e) {
                log.error("[Leaderboard Refresh] Critical failure during task execution.", e);
            }
        });
    }

    @Deprecated
    public void deprecatedRefreshRankedMembers() {
        redisLockExecutor.executeIfAcquired(LOCK_KEY, Duration.ofHours(1), () -> {
            LocalDate today = LocalDate.now();
            LocalDateTime from = today.minusDays(crewLeaderboardProperties.during().toDays()).atStartOfDay();
            LocalDateTime to = today.atStartOfDay().minusNanos(1);
            log.info("Starting To Renew Top Rankers - {} ~ {}", from, to);
            leaderboardRebuildService.renewTopRankers(from, to, crewLeaderboardProperties.rankLimit());
        });
    }
}
