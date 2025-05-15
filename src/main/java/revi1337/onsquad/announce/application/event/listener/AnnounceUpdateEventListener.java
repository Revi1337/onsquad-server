package revi1337.onsquad.announce.application.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.event.AnnounceUpdateEvent;
import revi1337.onsquad.announce.domain.AnnounceCacheRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceUpdateEventListener {

    private final AnnounceCacheRepository announceCacheRepository;

    @TransactionalEventListener
    public void handleAnnounceUpdateEvent(AnnounceUpdateEvent event) {
        log.debug("[{}] Renew fixed announces caches in crew_id = {}", event.getEventName(), event.crewId());
        announceCacheRepository.updateCacheByCrewIdAndId(event.crewId(), event.announceId());
        announceCacheRepository.updateCachesByCrewId(event.crewId());
    }
}
