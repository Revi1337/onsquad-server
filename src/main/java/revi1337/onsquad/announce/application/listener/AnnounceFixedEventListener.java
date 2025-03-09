package revi1337.onsquad.announce.application.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.common.config.RedisCacheManagerConfiguration.RedisCacheName;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceFixedEventListener {

    private final AnnounceRepository announceRepository;
    private final CacheManager redisCacheManager;

    @TransactionalEventListener
    public void handleAnnounceFixedEvent(AnnounceFixedEvent fixedEvent) {
        log.debug("[{}] Renew fixed announces caches in crew_id = {}", fixedEvent.getEventName(), fixedEvent.crewId());
        List<AnnounceInfoDomainDto> announceInfos = announceRepository
                .findLimitedAnnouncesByCrewId(fixedEvent.crewId());

        Cache cache = redisCacheManager.getCache(RedisCacheName.CREW_ANNOUNCES);
        cache.put(String.format("crew:%d", fixedEvent.crewId()), announceInfos);
    }
}
