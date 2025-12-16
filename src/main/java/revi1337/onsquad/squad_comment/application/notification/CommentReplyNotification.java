package revi1337.onsquad.squad_comment.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_comment.application.notification.CommentNotificationFetchResult.CommentReplyAddedNotificationResult;

@Getter
public class CommentReplyNotification extends AbstractNotification {

    private static final String MESSAGE_FORMAT = "%s 님이 대댓글을 남겼습니다.";
    private final CommentReplyPayload payload;

    public CommentReplyNotification(CommentReplyAddedNotificationResult notificationResult) {
        super(notificationResult.replyCommentWriterId(), notificationResult.parentCommentWriterId(), NotificationTopic.USER, NotificationDetail.COMMENT_REPLY);
        this.payload = new CommentReplyPayload(
                notificationResult.crewId(),
                notificationResult.crewName(),
                notificationResult.squadId(),
                notificationResult.squadTitle(),
                notificationResult.parentCommentId(),
                notificationResult.replyCommentId(),
                String.format(MESSAGE_FORMAT, notificationResult.replyCommentWriterNickname())
        );
    }

    public record CommentReplyPayload(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long parentId,
            Long replyId,
            String message
    ) {

    }
}
