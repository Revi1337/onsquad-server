package revi1337.onsquad.crew.application.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.application.CrewTopMemberRefreshService;
import revi1337.onsquad.crew.config.CrewTopMemberProperties;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewTopMemberRefreshScheduler { // TODO Scale-Out 상황이라면, 서버 수만큼 집계 쿼리가 실행됨 --> DB 부하. 따라서 Redis Lock으로 이를 통해 이를 방어할 수 있음.

    private final CrewTopMemberProperties crewTopMemberProperties;
    private final CrewTopMemberRefreshService crewTopMemberRefreshService;

    @Scheduled(cron = "${onsquad.api.crew-top-members.schedule.expression}")
    public void refreshTopMembers() {
        LocalDate to = LocalDate.now().minusDays(1);
        LocalDate from = to.minusDays(crewTopMemberProperties.during().toDays());
        log.info("Starting To Renew CrewTopMember - {} ~ {}", from, to);
        crewTopMemberRefreshService.refresh(from, to, crewTopMemberProperties.rankLimit());
    }
}
