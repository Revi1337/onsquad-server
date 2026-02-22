package revi1337.onsquad.announce.application.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.domain.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.event.AnnounceDeleteEvent;
import revi1337.onsquad.announce.domain.event.AnnouncePinnedEvent;
import revi1337.onsquad.announce.domain.event.AnnounceUpdateEvent;

/**
 * Event listener for announcement cache invalidation. Ensures data consistency by evicting stale caches based on a Cache-Aside strategy:
 * <ul>
 * <li><b>onCreate:</b> Evicts the list cache to reflect new content.</li>
 * <li><b>onUpdate:</b> Evicts both the specific item and the list cache.</li>
 * <li><b>onDelete:</b> Removes the specific item and evicts the list cache.</li>
 * <li><b>onPinned:</b> Evicts both the item and the list cache as the display order changes.</li>
 * </ul>
 * Note: Eviction (Invalidation) is used instead of immediate updates to minimize database I/O.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnnounceEventListener {

    private final AnnounceCacheService announceCacheService;

    @TransactionalEventListener
    public void onCreate(AnnounceCreateEvent event) {
        log.debug("[{}] Evicting list cache due to new announcement in crew: {}", event.getEventName(), event.crewId());
        announceCacheService.evictAnnounceLists(List.of(event.crewId()));
        announceCacheService.evictAnnounces(List.of(event.crewId()));
    }

    @TransactionalEventListener
    public void onUpdate(AnnounceUpdateEvent event) {
        log.debug("[{}] Evicting single & list cache due to update in crew: {}, announce: {}", event.getEventName(), event.crewId(), event.announceId());
        announceCacheService.evictAnnounce(event.crewId(), event.announceId());
        announceCacheService.evictAnnounceLists(List.of(event.crewId()));
    }

    @TransactionalEventListener
    public void onDelete(AnnounceDeleteEvent event) {
        log.debug("[{}] Evicting single & list cache due to deletion in crew: {}, announce: {}", event.getEventName(), event.crewId(), event.announceId());
        announceCacheService.evictAnnounce(event.crewId(), event.announceId());
        announceCacheService.evictAnnounceLists(List.of(event.crewId()));
    }

    @TransactionalEventListener
    public void onPinned(AnnouncePinnedEvent event) {
        log.debug("[{}] Evicting caches due to pin state change in crew: {}, announce: {}", event.getEventName(), event.crewId(), event.announceId());
        announceCacheService.evictAnnounce(event.crewId(), event.announceId());
        announceCacheService.evictAnnounceLists(List.of(event.crewId()));
    }
}
