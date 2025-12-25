package revi1337.onsquad.crew_member.application.initializer;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.crew_member.config.CrewTopMemberProperties;
import revi1337.onsquad.crew_member.domain.repository.top.CrewTopMemberRepository;
import revi1337.onsquad.crew_member.domain.result.Top5CrewMemberResult;

@Slf4j
@RequiredArgsConstructor
public class LocalCrewTopMemberInitializer {

    private final CrewTopMemberRepository crewTopMemberRepository;
    private final CrewTopMemberProperties crewTopMemberProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void initializedCrewTopMembers() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(crewTopMemberProperties.during().toDays());

        log.info("[Initialize Crew Top Members]");
        crewTopMemberRepository.deleteAllInBatch();
        crewTopMemberRepository.insertBatch(
                crewTopMemberRepository.fetchAggregatedTopMembers(from, to, crewTopMemberProperties.rankLimit()).stream()
                        .map(Top5CrewMemberResult::toEntity)
                        .toList()
        );
    }
}
