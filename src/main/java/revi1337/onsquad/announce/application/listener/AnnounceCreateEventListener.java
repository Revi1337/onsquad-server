package revi1337.onsquad.announce.application.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.common.constant.CacheConst;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceCreateEventListener {

    private final AnnounceRepository announceRepository;
    private final CacheManager caffeineCacheManager;

    @TransactionalEventListener
    public void handleAnnounceCreateEvent(AnnounceCreateEvent createEvent) {
        log.debug("[{}] Renew new announces caches in crew_id = {}", createEvent.getEventName(), createEvent.crewId());
        List<AnnounceInfoDomainDto> announceInfos = announceRepository
                .findLimitedAnnouncesByCrewId(createEvent.crewId());

        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCES);
        String computedCacheName = String.format("crew:%d", createEvent.crewId());
        cache.evict(computedCacheName);
        cache.put(computedCacheName, announceInfos);
    }
}
