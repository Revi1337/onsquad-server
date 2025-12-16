package revi1337.onsquad.squad_comment.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_comment.application.notification.CommentNotificationFetchResult.CommentAddedNotificationResult;

@Getter
public class CommentNotification extends AbstractNotification {

    private static final String MESSAGE_FORMAT = "%s 님이 스쿼드에 댓글을 남겼습니다.";
    private final CommentPayload payload;

    public CommentNotification(CommentAddedNotificationResult notificationResult) {
        super(notificationResult.commentWriterId(), notificationResult.squadMemberId(), NotificationTopic.USER, NotificationDetail.COMMENT);
        this.payload = new CommentPayload(
                notificationResult.crewId(),
                notificationResult.crewName(),
                notificationResult.squadId(),
                notificationResult.squadTitle(),
                notificationResult.commentId(),
                String.format(MESSAGE_FORMAT, notificationResult.commentWriterNickname())
        );
    }

    public record CommentPayload(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long commentId,
            String message
    ) {

    }
}
