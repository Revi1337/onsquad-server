package revi1337.onsquad.squad_comment.application.listener;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew.application.CrewRankingManager;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.squad_comment.application.history.CommentHistory;
import revi1337.onsquad.squad_comment.application.history.CommentReplyHistory;
import revi1337.onsquad.squad_comment.application.notification.CommentContextReader;
import revi1337.onsquad.squad_comment.application.notification.CommentNotification;
import revi1337.onsquad.squad_comment.application.notification.CommentReplyNotification;
import revi1337.onsquad.squad_comment.domain.event.CommentAdded;
import revi1337.onsquad.squad_comment.domain.event.CommentReplyAdded;

@RequiredArgsConstructor
@Component
public class SquadCommentEventListener {

    private final CrewRankingManager crewRankingManager;
    private final CommentContextReader commentNotificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onCommentAdded(CommentAdded added) {
        commentNotificationRepository.readAddedContext(added.writerId(), added.commentId())
                .ifPresent(context -> {
                    crewRankingManager.applyActivityScore(context.crewId(), context.commentWriterId(), Instant.now(), CrewActivity.SQUAD_COMMENT);
                    eventPublisher.publishEvent(new CommentHistory(context));
                    sendNotificationIfPossible(new CommentNotification(context));
                });
    }

    @TransactionalEventListener
    public void onCommentReplyAdded(CommentReplyAdded replyAdded) {
        commentNotificationRepository.readReplyAddedContext(replyAdded.parentId(), replyAdded.writerId(), replyAdded.replyId())
                .ifPresent(context -> {
                    crewRankingManager.applyActivityScore(context.crewId(), context.replyCommentWriterId(), Instant.now(), CrewActivity.SQUAD_COMMENT_REPLY);
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
