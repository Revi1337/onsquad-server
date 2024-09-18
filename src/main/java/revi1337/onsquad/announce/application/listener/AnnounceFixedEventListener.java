package revi1337.onsquad.announce.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.CacheManager;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.AnnounceRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnnounceFixedEventListener {

    private final AnnounceRepository announceRepository;
    private final CacheManager announceCacheManager;

    @TransactionalEventListener
    public void handleAnnounceFixedEvent(AnnounceFixedEvent fixedEvent) {
        List<AnnounceInfoDto> announceInfos = getAnnounceInfos(fixedEvent);
        log.debug("[{}] Renew fixed announces caches in crew_id = {}", fixedEvent.getEventName(), fixedEvent.crewId());
        announceCacheManager.cacheSpecificCrewAnnounceInfos(
                fixedEvent.crewId(),
                announceInfos
        );
    }

    private List<AnnounceInfoDto> getAnnounceInfos(AnnounceFixedEvent fixedEvent) {
        return announceRepository.findLimitedAnnouncesByCrewId(fixedEvent.crewId()).stream()
                .map(AnnounceInfoDto::from)
                .toList();
    }
}
