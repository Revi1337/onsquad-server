package revi1337.onsquad.announce.application.event.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.AnnounceQueryDslRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.common.constant.CacheConst;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceCreateEventListener {

    private final AnnounceQueryDslRepository announceQueryDslRepository;
    private final CacheManager caffeineCacheManager;

    @TransactionalEventListener
    public void handleAnnounceCreateEvent(AnnounceCreateEvent event) {
        log.debug("[{}] Renew new announces caches in crew_id = {}", event.getEventName(), event.crewId());
        List<AnnounceDomainDto> announceInfos = announceQueryDslRepository.fetchAllInDefaultByCrewId(event.crewId());

        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCES);
        if (cache != null) {
            String computedCacheName = String.format("crew:%d", event.crewId());
            cache.evict(computedCacheName);
            cache.put(computedCacheName, announceInfos);
        }
    }
}
