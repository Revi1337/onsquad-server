package revi1337.onsquad.backup.crew.application.initializer;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.backup.crew.config.property.CrewTopMemberProperty;
import revi1337.onsquad.backup.crew.domain.CrewTopMemberRepository;

@Slf4j
@RequiredArgsConstructor
public class NonLocalCrewTopMemberInitializer {

    private final CrewTopMemberRepository crewTopMemberRepository;
    private final CrewTopMemberProperty crewTopMemberProperty;

    @EventListener(ApplicationReadyEvent.class)
    public void initializedCrewTopMembers() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(crewTopMemberProperty.during().toDays());

        log.info("[Initialize Crew Top Members]");
        if (!crewTopMemberRepository.exists()) {
            crewTopMemberRepository.batchInsert(
                    crewTopMemberRepository.findAllTopNCrewMembers(from, to, crewTopMemberProperty.rankLimit())
            );
        }
    }
}
