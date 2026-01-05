package revi1337.onsquad.crew.application.listener;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.domain.result.AnnounceReference;
import revi1337.onsquad.crew.application.CrewRankingManager;
import revi1337.onsquad.crew.domain.event.CrewContextDisposed;
import revi1337.onsquad.infrastructure.aws.s3.event.FilesDeleteEvent;

@RequiredArgsConstructor
@Component
public class CrewContextEventListener {

    private final AnnounceCacheService announceCacheService;
    private final CrewRankingManager crewRankingManager;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onContextDisposed(CrewContextDisposed contextDisposed) {
        List<Long> crewIds = collectUniqueCrewIds(contextDisposed);
        announceCacheService.evictAnnounceListsByCrews(crewIds);
        announceCacheService.evictAnnouncesByReferences(contextDisposed.getAnnounceReferences());
        crewRankingManager.evictCrewsLeaderboard(contextDisposed.getDeletedCrewIds());
        eventPublisher.publishEvent(new FilesDeleteEvent(contextDisposed.getCrewImageUrls()));
    }

    private List<Long> collectUniqueCrewIds(CrewContextDisposed contextDisposed) {
        return Stream.concat(contextDisposed.getDeletedCrewIds().stream(), contextDisposed.getAnnounceReferences().stream().map(AnnounceReference::crewId))
                .distinct()
                .toList();
    }
}
