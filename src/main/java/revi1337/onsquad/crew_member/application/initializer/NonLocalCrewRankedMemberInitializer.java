package revi1337.onsquad.crew_member.application.initializer;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;

@Slf4j
@RequiredArgsConstructor
public class NonLocalCrewRankedMemberInitializer {

    private final CrewRankedMemberRepository crewRankedMemberRepository;
    private final CrewRankedMemberProperties crewRankedMemberProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCrewRankedMembers() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(crewRankedMemberProperties.during().toDays());

        log.info("[Initialize CrewRankedMembers]");
        if (!crewRankedMemberRepository.exists()) {
            crewRankedMemberRepository.insertBatch(
                    crewRankedMemberRepository.fetchAggregatedRankedMembers(from, to, crewRankedMemberProperties.rankLimit())
                            .stream()
                            .map(CrewRankedMemberResult::toEntity)
                            .toList()
            );
        }
    }
}
