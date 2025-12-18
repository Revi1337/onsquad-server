package revi1337.onsquad.squad_comment.application.history;

import lombok.Getter;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_comment.application.notification.CommentContext.CommentReplyAddedContext;

@Getter
public class CommentReplyHistory implements History {

    private static final String MESSAGE_FORMAT = "[%s | %s] 스쿼드에 대댓글을 남겼습니다.";

    private final Long memberId;
    private final Long crewId;
    private final Long squadId;
    private final Long squadCommentId;
    private final HistoryType type = HistoryType.SQUAD_COMMENT_REPLY;
    private final String message;

    public CommentReplyHistory(CommentReplyAddedContext context) {
        this.memberId = context.replyCommentWriterId();
        this.crewId = context.crewId();
        this.squadId = context.squadId();
        this.squadCommentId = context.replyCommentId();
        this.message = String.format(MESSAGE_FORMAT, context.crewName(), context.squadTitle());
    }

    @Override
    public HistoryEntity toEntity() {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .squadCommentId(squadCommentId)
                .type(type)
                .message(message)
                .build();
    }
}
