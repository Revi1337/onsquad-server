package revi1337.onsquad.member.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.event.MemberContextDisposed;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;
import revi1337.onsquad.token.application.RefreshTokenManager;

@Component
@RequiredArgsConstructor
public class MemberContextEventListener {

    private final HistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;
    private final RefreshTokenManager redisRefreshTokenManager;
    private final AnnounceCacheService announceCacheService;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onContextDisposed(MemberContextDisposed contextDisposed) {
        redisRefreshTokenManager.deleteTokenBy(contextDisposed.memberId());
        historyRepository.deleteByMemberId(contextDisposed.memberId());
        notificationRepository.deleteByReceiverId(contextDisposed.memberId());
        announceCacheService.evictAnnounceLists(contextDisposed.announceReferences().stream().map(AnnounceReference::crewId).toList());
        announceCacheService.evictAnnouncesByReferences(contextDisposed.announceReferences());
        if (contextDisposed.memberImageUrl() != null) {
            eventPublisher.publishEvent(new FileDeleteEvent(contextDisposed.memberImageUrl()));
        }
    }
}
