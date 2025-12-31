package revi1337.onsquad.crew_member.application.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.CrewRankedMemberRefreshService;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.infrastructure.redis.RedisLockExecutor;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewRankedMemberRefreshScheduler {

    private static final String LOCK_KEY = "refresh-sch-lock";

    private final CrewRankedMemberProperties crewRankedMemberProperties;
    private final CrewRankedMemberRefreshService crewRankedMemberRefreshService;
    private final RedisLockExecutor redisLockExecutor;

    @Scheduled(cron = "${onsquad.api.crew-rank-members.schedule.expression}")
    public void refreshRankedMembers() {
        redisLockExecutor.executeWithLock(LOCK_KEY, () -> {
            LocalDate to = LocalDate.now().minusDays(1);
            LocalDate from = to.minusDays(crewRankedMemberProperties.during().toDays());
            log.info("Starting To Renew CrewRankedMember - {} ~ {}", from, to);
            crewRankedMemberRefreshService.refresh(from, to, crewRankedMemberProperties.rankLimit());
        });
    }
}
