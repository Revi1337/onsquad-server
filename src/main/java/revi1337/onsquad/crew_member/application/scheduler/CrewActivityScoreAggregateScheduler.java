package revi1337.onsquad.crew_member.application.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardSynchronizeService;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;
import revi1337.onsquad.infrastructure.storage.redis.RedisLockExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewActivityScoreAggregateScheduler {

    private static final String LOCK_KEY = "refresh-sch-lock";

    private final CrewRankedMemberProperties crewRankedMemberProperties;
    private final CrewLeaderboardSynchronizeService leaderboardSynchronizeService;
    private final RedisLockExecutor redisLockExecutor;
    private final CrewLeaderboardManager leaderboardManager;

    @Scheduled(cron = "${onsquad.api.crew-rank-members.schedule.expression}")
    public void refreshRankedMembers() {
        redisLockExecutor.executeIfAcquired(LOCK_KEY, Duration.ofHours(1), () -> {
            List<CrewRankedMemberResult> previousRankedMembers = leaderboardSynchronizeService.getCurrentLeaderboard();
            leaderboardManager.backupPreviousLeaderboard(previousRankedMembers);
            List<CrewRankedMemberResult> currentRankedMembers = leaderboardManager.getLeaderboard(crewRankedMemberProperties.rankLimit());
            leaderboardSynchronizeService.synchronize(currentRankedMembers);
        });
    }

    @Deprecated
    public void deprecatedRefreshRankedMembers() {
        redisLockExecutor.executeIfAcquired(LOCK_KEY, Duration.ofHours(1), () -> {
            LocalDate today = LocalDate.now();
            LocalDateTime from = today.minusDays(crewRankedMemberProperties.during().toDays()).atStartOfDay();
            LocalDateTime to = today.atStartOfDay().minusNanos(1);
            log.info("Starting To Renew CrewRankedMember - {} ~ {}", from, to);
            leaderboardSynchronizeService.synchronize(from, to, crewRankedMemberProperties.rankLimit());
        });
    }
}
