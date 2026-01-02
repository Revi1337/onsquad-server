package revi1337.onsquad.member.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.infrastructure.aws.s3.event.FilesDeleteEvent;
import revi1337.onsquad.member.domain.event.MemberDeleteEvent;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;
import revi1337.onsquad.token.application.RefreshTokenManager;

@RequiredArgsConstructor
@Component
public class MemberDeleteEventListener {

    private final HistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;
    private final RefreshTokenManager redisRefreshTokenManager;
    private final AnnounceCacheService announceCacheService;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onDelete(MemberDeleteEvent event) {
        redisRefreshTokenManager.deleteTokenBy(event.memberId());

        historyRepository.deleteByMemberId(event.memberId());
        notificationRepository.deleteByReceiverId(event.memberId());

        announceCacheService.evictAllAnnounceCaches();
        if (!event.imageUrls().isEmpty()) {
            eventPublisher.publishEvent(new FilesDeleteEvent(event.imageUrls()));
        }
    }
}
