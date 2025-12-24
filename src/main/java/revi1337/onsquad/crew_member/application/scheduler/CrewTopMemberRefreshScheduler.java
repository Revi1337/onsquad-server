package revi1337.onsquad.crew_member.application.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.CrewTopMemberRefreshService;
import revi1337.onsquad.crew_member.config.CrewTopMemberProperties;
import revi1337.onsquad.infrastructure.redis.RedisLockExecutor;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewTopMemberRefreshScheduler {

    private static final String LOCK_KEY = "refresh-sch-lock";

    private final CrewTopMemberProperties crewTopMemberProperties;
    private final CrewTopMemberRefreshService crewTopMemberRefreshService;
    private final RedisLockExecutor redisLockExecutor;

    @Scheduled(cron = "${onsquad.api.crew-top-members.schedule.expression}")
    public void refreshTopMembers() {
        redisLockExecutor.executeWithLock(LOCK_KEY, () -> {
            LocalDate to = LocalDate.now().minusDays(1);
            LocalDate from = to.minusDays(crewTopMemberProperties.during().toDays());
            log.info("Starting To Renew CrewTopMember - {} ~ {}", from, to);
            crewTopMemberRefreshService.refresh(from, to, crewTopMemberProperties.rankLimit());
        });
    }
}
