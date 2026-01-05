package revi1337.onsquad.member.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.history.domain.repository.HistoryRepository;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;
import revi1337.onsquad.member.domain.event.MemberDeleted;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;
import revi1337.onsquad.token.application.RefreshTokenManager;

@RequiredArgsConstructor
@Component
public class MemberDeleteEventListener {

    private final HistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;
    private final RefreshTokenManager redisRefreshTokenManager;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onDeleted(MemberDeleted deleted) {
        redisRefreshTokenManager.deleteTokenBy(deleted.memberId());
        historyRepository.deleteByMemberId(deleted.memberId());
        notificationRepository.deleteByReceiverId(deleted.memberId());
        if (deleted.memberImageUrl() != null) {
            eventPublisher.publishEvent(new FileDeleteEvent(deleted.memberImageUrl()));
        }
    }
}
