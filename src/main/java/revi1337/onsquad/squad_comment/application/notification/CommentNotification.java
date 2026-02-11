package revi1337.onsquad.squad_comment.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentAddedContext;

@Getter
public class CommentNotification extends AbstractNotification {

    private final CommentPayload payload;

    public CommentNotification(CommentAddedContext context) {
        super(context.commentWriterId(), context.squadMemberId(), NotificationTopic.USER, NotificationDetail.COMMENT);
        this.payload = new CommentPayload(
                context.crewId(),
                context.crewName(),
                context.squadId(),
                context.squadTitle(),
                context.commentId(),
                NotificationDetail.COMMENT.formatMessage(context.commentWriterNickname())
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
