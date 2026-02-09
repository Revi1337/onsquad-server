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
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.model.CrewRankedMemberDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;

@Slf4j
@Profile({"local", "default"})
@ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
@Component
@RequiredArgsConstructor
public class CrewRankerRdbBootstrapper {

    private final CrewRankedMemberRepository crewRankedMemberRepository;
    private final CrewRankedMemberProperties crewRankedMemberProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void bootstrapRankedMembers() {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.toLocalDate().minusDays(crewRankedMemberProperties.during().toDays()).atStartOfDay();

        log.info("[RDB-Bootstrap] Starting ranking data aggregation and table initialization for local environment.");
        crewRankedMemberRepository.deleteAllInBatch();
        List<CrewRankedMember> aggregated = crewRankedMemberRepository.fetchAggregatedRankedMembers(from, to, crewRankedMemberProperties.rankLimit())
                .stream()
                .map(CrewRankedMemberDetail::toEntity)
                .toList();

        crewRankedMemberRepository.insertBatch(aggregated);
        log.info("[RDB-Bootstrap] Ranking table initialized with {} records.", aggregated.size());
    }
}
