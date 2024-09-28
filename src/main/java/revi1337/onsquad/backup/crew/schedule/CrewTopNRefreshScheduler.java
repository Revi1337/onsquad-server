package revi1337.onsquad.backup.crew.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import revi1337.onsquad.backup.crew.domain.CrewTopCache;
import revi1337.onsquad.backup.crew.domain.CrewTopCacheRepository;
import revi1337.onsquad.common.config.properties.ApiProperties;
import revi1337.onsquad.crew_member.domain.CrewMemberJdbcRepository;

import java.time.LocalDate;

import static revi1337.onsquad.common.config.properties.ApiProperties.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CrewTopNRefreshScheduler {

    private final CrewTopCacheRepository crewTopCacheRepository;
    private final CrewMemberJdbcRepository crewMemberJdbcRepository;
    private final ApiProperties apiProperties;

    @Scheduled(cron = "${onsquad.schedule.refresh-crew-top-n.expression}", scheduler = "crewTopTask")
    public void refreshTopNMemberInCrew() {
        CrewTopN crewTopNMetadata = apiProperties.crewTopN();
        LocalDate to = LocalDate.now().minusDays(1);
        LocalDate from = to.minusDays(apiProperties.crewTopN().cycle().toDays());

        log.info("[Renew CrewTopN Caches In DataBase : {} ~ {}]", from, to);
        crewTopCacheRepository.deleteAllInBatch();
        crewTopCacheRepository.batchInsertCrewTop(
                crewMemberJdbcRepository.findAllTopNCrewMembers(from, to, crewTopNMetadata.nSize()).stream()
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
