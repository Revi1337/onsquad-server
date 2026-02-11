package revi1337.onsquad.squad_comment.application.notification;

import lombok.Getter;
import revi1337.onsquad.notification.domain.AbstractNotification;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.squad_comment.domain.model.SquadCommentContext.CommentReplyAddedContext;

@Getter
public class CommentReplyNotification extends AbstractNotification {

    private final CommentReplyPayload payload;

    public CommentReplyNotification(CommentReplyAddedContext context) {
        super(context.replyCommentWriterId(), context.parentCommentWriterId(), NotificationTopic.USER, NotificationDetail.COMMENT_REPLY);
        this.payload = new CommentReplyPayload(
                context.crewId(),
                context.crewName(),
                context.squadId(),
                context.squadTitle(),
                context.parentCommentId(),
                context.replyCommentId(),
                NotificationDetail.COMMENT_REPLY.formatMessage(context.replyCommentWriterNickname())
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
