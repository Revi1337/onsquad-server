package revi1337.onsquad.crew.application.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.config.CrewTopMemberProperties;
import revi1337.onsquad.crew.domain.dto.top.Top5CrewMemberDomainDto;
import revi1337.onsquad.crew.domain.repository.top.CrewTopMemberRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewTopMemberRefreshScheduler {

    private final CrewTopMemberRepository crewTopMemberRepository;
    private final CrewTopMemberProperties crewTopMemberProperties;

    @Scheduled(cron = "${onsquad.api.crew-top-members.schedule.expression}", scheduler = "crewTopTask")
    public void refreshTopMembersInCrew() {
        LocalDate to = LocalDate.now().minusDays(1);
        LocalDate from = to.minusDays(crewTopMemberProperties.during().toDays());

        log.info("[Renew CrewTopMember Caches In DataBase : {} ~ {}]", from, to);
        crewTopMemberRepository.deleteAllInBatch();
        crewTopMemberRepository.batchInsert(
                crewTopMemberRepository.fetchAggregatedTopMembers(from, to, crewTopMemberProperties.rankLimit()).stream()
                        .map(Top5CrewMemberDomainDto::toEntity)
                        .toList()
        );
    }

    @Bean(name = "crewTopTask") // TODO 스케줄러 스레드 전용 설정파일로 분리 필요.
    public TaskScheduler configureTasks() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("crew-top-sch-");
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
