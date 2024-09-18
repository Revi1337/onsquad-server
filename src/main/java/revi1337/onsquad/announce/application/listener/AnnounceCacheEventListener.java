package revi1337.onsquad.announce.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.announce.application.CacheManager;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.announce.application.event.AnnounceCacheEvent;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceCacheEventListener {

    private final CacheManager announceCacheManager;

    @EventListener
    public void handleAnnounceCacheEvent(AnnounceCacheEvent cacheEvent) {
        List<AnnounceInfoDto> announceInfos = cacheEvent.announceInfos();
        if (!announceInfos.isEmpty()) {
            log.debug("[{}] Store announces caches in crew_id = {}", cacheEvent.getEventName(), cacheEvent.crewId());
            announceCacheManager.cacheSpecificCrewAnnounceInfos(cacheEvent.crewId(), announceInfos);
        } else {
            log.debug("[{}] Trying to cache announces. but empty in crew_id = {}", cacheEvent.getEventName(), cacheEvent.crewId());
        }
    }
}
