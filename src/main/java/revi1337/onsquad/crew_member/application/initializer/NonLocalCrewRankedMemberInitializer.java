package revi1337.onsquad.crew_member.application.initializer;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.crew_member.domain.model.CrewRankedMemberDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;

@Slf4j
@RequiredArgsConstructor
public class NonLocalCrewRankedMemberInitializer {

    private final CrewRankedMemberRepository crewRankedMemberRepository;
    private final CrewRankedMemberProperties crewRankedMemberProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCrewRankedMembers() {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.toLocalDate().minusDays(crewRankedMemberProperties.during().toDays()).atStartOfDay();

        log.info("[Initialize CrewRankedMembers]");
        if (!crewRankedMemberRepository.exists()) {
            crewRankedMemberRepository.insertBatch(
                    crewRankedMemberRepository.fetchAggregatedRankedMembers(from, to, crewRankedMemberProperties.rankLimit())
                            .stream()
                            .map(CrewRankedMemberDetail::toEntity)
                            .toList()
            );
        }
    }
}
