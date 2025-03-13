package revi1337.onsquad.backup.crew.config;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.backup.crew.domain.CrewTopCache;
import revi1337.onsquad.backup.crew.domain.CrewTopCacheRepository;
import revi1337.onsquad.common.config.properties.ApiProperties;
import revi1337.onsquad.common.config.properties.ApiProperties.CrewTopMembers;
import revi1337.onsquad.crew_member.domain.CrewMemberJdbcRepository;

@Slf4j
@ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
@Profile({"local", "default"})
@RequiredArgsConstructor
@Configuration
public class ConditionalCrewTopNCacheInitializer {

    private final CrewTopCacheRepository crewTopCacheRepository;
    private final CrewMemberJdbcRepository crewMemberJdbcRepository;
    private final ApiProperties apiProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void crewTop5Init() {
        CrewTopMembers crewTopMembersMetadata = apiProperties.crewTopMembers();
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(apiProperties.crewTopMembers().during().toDays());

        log.info("[Initialize Crew Top N Caches]");
        crewTopCacheRepository.deleteAllInBatch();
        crewTopCacheRepository.batchInsertCrewTop(
                crewMemberJdbcRepository.findAllTopNCrewMembers(from, to, crewTopMembersMetadata.rankLimit()).stream()
                        .map(CrewTopCache::from)
                        .toList()
        );
    }
}
