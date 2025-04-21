package revi1337.onsquad.backup.crew.application.initializer;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.backup.crew.domain.CrewTopCache;
import revi1337.onsquad.backup.crew.domain.CrewTopCacheRepository;
import revi1337.onsquad.crew_member.config.CrewTopMemberProperty;
import revi1337.onsquad.crew_member.domain.CrewMemberJdbcRepository;

@Slf4j
@ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
@Profile({"local", "default"})
@RequiredArgsConstructor
@Component
public class ConditionalCrewTopMemberCacheInitializer {

    private final CrewTopCacheRepository crewTopCacheRepository;
    private final CrewMemberJdbcRepository crewMemberJdbcRepository;
    private final CrewTopMemberProperty crewTopMemberProperty;

    @EventListener(ApplicationReadyEvent.class)
    public void initializedCachedCrewTopMembers() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(crewTopMemberProperty.during().toDays());

        log.info("[Initialize Crew Top N Caches]");
        crewTopCacheRepository.deleteAllInBatch();
        crewTopCacheRepository.batchInsertCrewTop(
                crewMemberJdbcRepository.findAllTopNCrewMembers(from, to, crewTopMemberProperty.rankLimit()).stream()
                        .map(CrewTopCache::from)
                        .toList()
        );
    }
}
