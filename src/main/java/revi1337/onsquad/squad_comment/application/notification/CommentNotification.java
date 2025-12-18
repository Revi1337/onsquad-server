package revi1337.onsquad.squad_comment.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_comment.application.notification.CommentContext.CommentAddedContext;

@Getter
public class CommentNotification extends AbstractNotification {

    private static final String MESSAGE_FORMAT = "%s 님이 스쿼드에 댓글을 남겼습니다.";
    private final CommentPayload payload;

    public CommentNotification(CommentAddedContext context) {
        super(context.commentWriterId(), context.squadMemberId(), NotificationTopic.USER, NotificationDetail.COMMENT);
        this.payload = new CommentPayload(
                context.crewId(),
                context.crewName(),
                context.squadId(),
                context.squadTitle(),
                context.commentId(),
                String.format(MESSAGE_FORMAT, context.commentWriterNickname())
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
