package revi1337.onsquad.crew.application.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.crew.domain.event.CrewContextDisposed;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardSnapshotManager;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;

@ExtendWith(MockitoExtension.class)
class CrewContextEventListenerTest {

    @Mock
    private AnnounceCacheService announceCacheService;

    @Mock
    private CrewLeaderboardManager crewLeaderboardManager;

    @Mock
    private CrewLeaderboardSnapshotManager crewLeaderboardSnapshotManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CrewContextEventListener crewContextEventListener;

    @Test
    void onContextDisposed() {
        List<Long> crewIds = List.of(1L, 2L);
        List<String> imageUrls = List.of("url1", "url2");
        CrewContextDisposed event = new CrewContextDisposed(crewIds, imageUrls);

        crewContextEventListener.onContextDisposed(event);

        verify(announceCacheService).evictAnnounceLists(crewIds);
        verify(announceCacheService).evictAnnounces(crewIds);
        verify(crewLeaderboardManager).removeLeaderboards(crewIds);
        verify(crewLeaderboardSnapshotManager).removeSnapshots(crewIds);
        ArgumentCaptor<FileDeleteEvent> fileEventCaptor = ArgumentCaptor.forClass(FileDeleteEvent.class);
        verify(eventPublisher).publishEvent(fileEventCaptor.capture());
        assertThat(fileEventCaptor.getValue().getFileUrls()).isEqualTo(imageUrls);
    }
}
