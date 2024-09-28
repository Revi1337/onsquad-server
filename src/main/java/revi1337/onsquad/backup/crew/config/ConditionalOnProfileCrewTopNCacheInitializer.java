package revi1337.onsquad.backup.crew.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.backup.crew.domain.CrewTopCache;
import revi1337.onsquad.backup.crew.domain.CrewTopCacheRepository;
import revi1337.onsquad.common.config.properties.ApiProperties;
import revi1337.onsquad.crew_member.domain.CrewMemberJdbcRepository;

import java.time.LocalDate;

@Profile({"local", "default"})
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ConditionalOnProfileCrewTopNCacheInitializer {

    private final CrewTopCacheRepository crewTopCacheRepository;
    private final CrewMemberJdbcRepository crewMemberJdbcRepository;
    private final ApiProperties apiProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void crewTop5Init() {
        ApiProperties.CrewTopN crewTopNMetadata = apiProperties.crewTopN();
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(apiProperties.crewTopN().cycle().toDays());

        log.info("[Initialize Crew Top N Caches]");
        crewTopCacheRepository.deleteAllInBatch();
        crewTopCacheRepository.batchInsertCrewTop(
                crewMemberJdbcRepository.findAllTopNCrewMembers(from, to, crewTopNMetadata.nSize()).stream()
                        .map(CrewTopCache::from)
                        .toList()
        );
    }
}
