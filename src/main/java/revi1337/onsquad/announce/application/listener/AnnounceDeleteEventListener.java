package revi1337.onsquad.announce.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.domain.event.AnnounceDeleteEvent;
import revi1337.onsquad.announce.domain.repository.AnnounceCacheRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceDeleteEventListener {

    private final AnnounceCacheRepository announceCacheRepository;

    @TransactionalEventListener
    public void handleAnnounceDeleteEvent(AnnounceDeleteEvent event) {
        log.debug("[{}] Renew fixed announces caches in crew_id = {}", event.getEventName(), event.crewId());
        announceCacheRepository.clearCacheByCrewIdAndId(event.crewId(), event.announceId());
        announceCacheRepository.updateCachesByCrewId(event.crewId());
    }
}
