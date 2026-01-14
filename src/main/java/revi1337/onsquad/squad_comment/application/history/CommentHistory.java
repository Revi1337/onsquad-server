package revi1337.onsquad.squad_comment.application.history;

import java.time.LocalDateTime;
import lombok.Getter;
import revi1337.onsquad.history.application.History;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_comment.application.notification.CommentContext.CommentAddedContext;

@Getter
public class CommentHistory implements History {

    private static final String MESSAGE_FORMAT = "[%s | %s] 스쿼드에 댓글을 남겼습니다.";

    private final Long memberId;
    private final Long crewId;
    private final Long squadId;
    private final Long squadCommentId;
    private final HistoryType type = HistoryType.SQUAD_COMMENT;
    private final String message;
    private final LocalDateTime timeStamp;

    public CommentHistory(CommentAddedContext context) {
        this.memberId = context.commentWriterId();
        this.crewId = context.crewId();
        this.squadId = context.squadId();
        this.squadCommentId = context.commentId();
        this.message = String.format(MESSAGE_FORMAT, context.crewName(), context.squadTitle());
        this.timeStamp = LocalDateTime.now();
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
                .recordedAt(timeStamp)
                .build();
    }
}
