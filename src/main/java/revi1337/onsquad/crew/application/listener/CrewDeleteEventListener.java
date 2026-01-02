package revi1337.onsquad.crew.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.crew.domain.event.CrewDeleteEvent;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;

@RequiredArgsConstructor
@Component
public class CrewDeleteEventListener {

    private final AnnounceCacheService announceCacheService;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onDelete(CrewDeleteEvent event) {
        announceCacheService.evictAnnounceCache(event.crewId());
        if (event.imageUrl() != null) {
            eventPublisher.publishEvent(new FileDeleteEvent(event.imageUrl()));
        }
    }
}
