package revi1337.onsquad.crew.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.crew.domain.event.CrewContextDisposed;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardSnapshotManager;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;

@Component
@RequiredArgsConstructor
public class CrewContextEventListener {

    private final AnnounceCacheService announceCacheService;
    private final CrewLeaderboardManager crewLeaderboardManager;
    private final CrewLeaderboardSnapshotManager crewLeaderboardSnapshotManager;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onContextDisposed(CrewContextDisposed contextDisposed) {
        eventPublisher.publishEvent(new FileDeleteEvent(contextDisposed.getCrewImageUrls()));
        announceCacheService.evictAnnounceLists(contextDisposed.getDeletedCrewIds());
        announceCacheService.evictAnnounces(contextDisposed.getDeletedCrewIds());
        crewLeaderboardManager.removeLeaderboards(contextDisposed.getDeletedCrewIds());
        crewLeaderboardSnapshotManager.removeSnapshots(contextDisposed.getDeletedCrewIds());
    }
}
