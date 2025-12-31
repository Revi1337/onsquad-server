package revi1337.onsquad.crew_member.application.initializer;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;

@Slf4j
@RequiredArgsConstructor
public class LocalCrewRankedMemberInitializer {

    private final CrewRankedMemberRepository crewRankedMemberRepository;
    private final CrewRankedMemberProperties crewRankedMemberProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCrewRankedMembers() {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.toLocalDate().minusDays(crewRankedMemberProperties.during().toDays()).atStartOfDay();

        log.info("[Initialize CrewRankedMembers]");
        crewRankedMemberRepository.deleteAllInBatch();
        crewRankedMemberRepository.insertBatch(
                crewRankedMemberRepository.fetchAggregatedRankedMembers(from, to, crewRankedMemberProperties.rankLimit()).stream()
                        .map(CrewRankedMemberResult::toEntity)
                        .toList()
        );
    }
}
