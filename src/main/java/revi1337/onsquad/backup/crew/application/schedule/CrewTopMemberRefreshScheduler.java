package revi1337.onsquad.backup.crew.application.schedule;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import revi1337.onsquad.backup.crew.config.property.CrewTopMemberProperty;
import revi1337.onsquad.backup.crew.domain.CrewTopMemberRepository;
import revi1337.onsquad.backup.crew.domain.dto.Top5CrewMemberDomainDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewTopMemberRefreshScheduler {

    private final CrewTopMemberRepository crewTopMemberRepository;
    private final CrewTopMemberProperty crewTopMemberProperty;

    @Scheduled(cron = "${onsquad.api.crew-top-members.schedule.expression}", scheduler = "crewTopTask")
    public void refreshTopMembersInCrew() {
        LocalDate to = LocalDate.now().minusDays(1);
        LocalDate from = to.minusDays(crewTopMemberProperty.during().toDays());

        log.info("[Renew CrewTopMember Caches In DataBase : {} ~ {}]", from, to);
        crewTopMemberRepository.deleteAllInBatch();
        crewTopMemberRepository.batchInsert(
                crewTopMemberRepository.fetchAggregatedTopMembers(from, to, crewTopMemberProperty.rankLimit()).stream()
                        .map(Top5CrewMemberDomainDto::toEntity)
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
