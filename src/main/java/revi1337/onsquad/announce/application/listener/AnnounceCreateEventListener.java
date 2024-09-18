package revi1337.onsquad.announce.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.CacheManager;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.AnnounceRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceCreateEventListener {

    private final AnnounceRepository announceRepository;
    private final CacheManager announceCacheManager;

    @TransactionalEventListener
    public void handleAnnounceCreateEvent(AnnounceCreateEvent createEvent) {
        List<AnnounceInfoDto> announceInfos = getAnnounceInfos(createEvent);
        log.debug("[{}] Renew new announces caches in crew_id = {}", createEvent.getEventName(), createEvent.crewId());
        announceCacheManager.cacheSpecificCrewAnnounceInfos(
                createEvent.crewId(),
                announceInfos
        );
    }

    private List<AnnounceInfoDto> getAnnounceInfos(AnnounceCreateEvent createEvent) {
        return announceRepository.findLimitedAnnouncesByCrewId(createEvent.crewId()).stream()
                .map(AnnounceInfoDto::from)
                .toList();
    }
}
