package revi1337.onsquad.squad_comment.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.squad_comment.application.notification.CommentNotification;
import revi1337.onsquad.squad_comment.application.notification.CommentNotificationFetcher;
import revi1337.onsquad.squad_comment.application.notification.CommentReplyNotification;
import revi1337.onsquad.squad_comment.domain.event.CommentAdded;
import revi1337.onsquad.squad_comment.domain.event.CommentReplyAdded;

@RequiredArgsConstructor
@Component
public class SquadCommentEventListener {

    private final CommentNotificationFetcher commentNotificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onCommentAdded(CommentAdded added) {
        commentNotificationRepository.fetchAddedInformation(added.writerId(), added.commentId())
                .ifPresent(notificationResult -> {
                    CommentNotification notification = new CommentNotification(notificationResult);
                    sendIfPossible(notification);
                });
    }

    @TransactionalEventListener
    public void onCommentReplyAdded(CommentReplyAdded replyAdded) {
        commentNotificationRepository.fetchReplyAddedInformation(replyAdded.parentId(), replyAdded.writerId(), replyAdded.replyId())
                .ifPresent(notificationResult -> {
                    CommentReplyNotification notification = new CommentReplyNotification(notificationResult);
                    sendIfPossible(notification);
                });
    }

    private void sendIfPossible(Notification notification) {
        if (notification.shouldSend()) {
            eventPublisher.publishEvent(notification);
        }
    }
}
