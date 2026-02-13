package revi1337.onsquad.crew_member.application.initializer;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.config.CrewLeaderboardProperties;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;

@Slf4j
@Profile({"local", "default"})
@ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
@Component
@RequiredArgsConstructor
public class CrewLeaderboardBootstrapper {

    private final CrewRankerRepository crewRankerRepository;
    private final CrewLeaderboardProperties crewLeaderboardProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void bootstrapLeaderboard() {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.toLocalDate().minusDays(crewLeaderboardProperties.during().toDays()).atStartOfDay();

        log.info("[Crew Leaderboard Bootstrap] Starting ranking data aggregation and table initialization for local environment.");
        crewRankerRepository.deleteAllInBatch();
        List<CrewRankerCandidate> candidates = crewRankerRepository.fetchAggregatedRankedMembers(from, to, crewLeaderboardProperties.rankLimit());
        crewRankerRepository.insertBatch(candidates);
        log.info("[Crew Leaderboard Bootstrap] Ranking table initialized with {} records.", candidates.size());
    }
}
