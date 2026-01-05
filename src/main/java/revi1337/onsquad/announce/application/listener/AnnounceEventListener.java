package revi1337.onsquad.announce.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.domain.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.event.AnnounceDeleteEvent;
import revi1337.onsquad.announce.domain.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.event.AnnounceUpdateEvent;

/**
 * Event listener responsible for synchronizing announcement caches. This component listens for various announcement-related events and updates the cache using
 * the following strategies:
 * <ul>
 * <li><b>onCreate:</b> Refreshes the default announcement list cache to include the newly created item.
 * Note: Individual item caching is deferred until the first access (Cache-Aside) to save resources,
 * unless a massive simultaneous access (Thundering Herd) is expected via push notifications.</li>
 * <li><b>onUpdate:</b> Updates both the specific announcement cache and the overall default list cache
 * to reflect content changes.</li>
 * <li><b>onDelete:</b> Removes the specific announcement from the cache and refreshes the default list cache.</li>
 * <li><b>onFixed:</b> Updates both the specific announcement and the default list cache,
 * <p>
 * as the pinning state affects the display order.</li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceEventListener {

    private final AnnounceCacheService announceCacheService;

    @TransactionalEventListener
    public void onCreate(AnnounceCreateEvent event) {
        log.debug("[{}] Refreshing list cache for crew: {}", event.getEventName(), event.crewId());
        announceCacheService.putDefaultAnnounceList(event.crewId());
    }

    @TransactionalEventListener
    public void onUpdate(AnnounceUpdateEvent event) {
        log.debug("[{}] Refreshing single & list cache for crew: {}", event.getEventName(), event.crewId());
        announceCacheService.putAnnounce(event.crewId(), event.announceId());
        announceCacheService.putDefaultAnnounceList(event.crewId());
    }

    @TransactionalEventListener
    public void onDelete(AnnounceDeleteEvent event) {
        log.debug("[{}] Evicting single and refreshing list cache for crew: {}", event.getEventName(), event.crewId());
        announceCacheService.evictAnnounce(event.crewId(), event.announceId());
        announceCacheService.putDefaultAnnounceList(event.crewId());
    }

    @TransactionalEventListener
    public void onFixed(AnnounceFixedEvent event) {
        log.debug("[{}] Refreshing fixed state caches for crew: {}", event.getEventName(), event.crewId());
        announceCacheService.evictAnnounce(event.crewId(), event.announceId());
        announceCacheService.putDefaultAnnounceList(event.crewId());
    }
}
