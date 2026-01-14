package revi1337.onsquad.squad_comment.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew.domain.event.ScoreIncreased;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.squad_comment.application.history.CommentHistory;
import revi1337.onsquad.squad_comment.application.history.CommentReplyHistory;
import revi1337.onsquad.squad_comment.application.notification.CommentContextReader;
import revi1337.onsquad.squad_comment.application.notification.CommentNotification;
import revi1337.onsquad.squad_comment.application.notification.CommentReplyNotification;
import revi1337.onsquad.squad_comment.domain.event.CommentAdded;
import revi1337.onsquad.squad_comment.domain.event.CommentReplyAdded;

@Component
@RequiredArgsConstructor
public class SquadCommentEventListener {

    private final CommentContextReader commentNotificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onCommentAdded(CommentAdded added) {
        commentNotificationRepository.readAddedContext(added.writerId(), added.commentId())
                .ifPresent(context -> {
                    eventPublisher.publishEvent(new ScoreIncreased(context.crewId(), context.commentWriterId(), CrewActivity.SQUAD_COMMENT));
                    eventPublisher.publishEvent(new CommentHistory(context));
                    sendNotificationIfPossible(new CommentNotification(context));
                });
    }

    @TransactionalEventListener
    public void onCommentReplyAdded(CommentReplyAdded replyAdded) {
        commentNotificationRepository.readReplyAddedContext(replyAdded.parentId(), replyAdded.writerId(), replyAdded.replyId())
                .ifPresent(context -> {
                    eventPublisher.publishEvent(new ScoreIncreased(context.crewId(), context.replyCommentWriterId(), CrewActivity.SQUAD_COMMENT_REPLY));
                    eventPublisher.publishEvent(new CommentReplyHistory(context));
                    sendNotificationIfPossible(new CommentReplyNotification(context));
                });
    }

    private void sendNotificationIfPossible(Notification notification) {
        if (notification.shouldSend()) {
            eventPublisher.publishEvent(notification);
        }
    }
}
