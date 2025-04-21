package revi1337.onsquad.backup.crew.application.schedule;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import revi1337.onsquad.backup.crew.domain.CrewTopCache;
import revi1337.onsquad.backup.crew.domain.CrewTopCacheRepository;
import revi1337.onsquad.crew_member.config.CrewTopMemberProperty;
import revi1337.onsquad.crew_member.domain.CrewMemberJdbcRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewTopNRefreshScheduler {

    private final CrewTopCacheRepository crewTopCacheRepository;
    private final CrewMemberJdbcRepository crewMemberJdbcRepository;
    private final CrewTopMemberProperty crewTopMemberProperty;

    @Scheduled(cron = "${onsquad.api.crew-top-members.schedule.expression}", scheduler = "crewTopTask")
    public void refreshTopNMemberInCrew() {
        LocalDate to = LocalDate.now().minusDays(1);
        LocalDate from = to.minusDays(crewTopMemberProperty.during().toDays());

        log.info("[Renew CrewTopN Caches In DataBase : {} ~ {}]", from, to);
        crewTopCacheRepository.deleteAllInBatch();
        crewTopCacheRepository.batchInsertCrewTop(
                crewMemberJdbcRepository.findAllTopNCrewMembers(from, to, crewTopMemberProperty.rankLimit())
                        .stream()
                        .map(CrewTopCache::from)
                        .toList()
        );
    }

    @Bean(name = "crewTopTask")
    public TaskScheduler configureTasks() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("crew-top-sch-");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
