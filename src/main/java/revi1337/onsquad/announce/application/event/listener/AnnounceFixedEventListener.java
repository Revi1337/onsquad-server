package revi1337.onsquad.announce.application.event.listener;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.AnnounceQueryDslRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.common.constant.CacheConst;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceFixedEventListener {

    private final AnnounceQueryDslRepository announceQueryDslRepository;
    private final CacheManager caffeineCacheManager;

    @TransactionalEventListener
    public void handleAnnounceFixedEvent(AnnounceFixedEvent event) {
        log.debug("[{}] Renew fixed announces caches in crew_id = {}", event.getEventName(), event.crewId());
        updateAnnounceCache(event);
        updateAnnouncesCache(event);
    }

    private void updateAnnounceCache(AnnounceFixedEvent event) {
        Optional<AnnounceDomainDto> announceInfo = announceQueryDslRepository
                .fetchByCrewIdAndId(event.crewId(), event.announceId());

        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCE);
        if (cache != null) {
            String computedCacheName = String.format("crew:%d:announce:%d", event.crewId(), event.announceId());
            cache.evict(computedCacheName);
            cache.put(computedCacheName, announceInfo.get());
        }
    }

    private void updateAnnouncesCache(AnnounceFixedEvent event) {
        List<AnnounceDomainDto> announceInfos = announceQueryDslRepository.fetchAllInDefaultByCrewId(event.crewId());

        Cache cache = caffeineCacheManager.getCache(CacheConst.CREW_ANNOUNCES);
        if (cache != null) {
            String computedCacheName = String.format("crew:%d", event.crewId());
            cache.evict(computedCacheName);
            cache.put(computedCacheName, announceInfos);
        }
    }
}
