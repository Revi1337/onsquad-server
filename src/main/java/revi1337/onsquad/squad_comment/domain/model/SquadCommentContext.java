package revi1337.onsquad.squad_comment.domain.model;

public class SquadCommentContext {

    public record CommentAddedContext(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long squadMemberId,
            Long commentId,
            Long commentWriterId,
            String commentWriterNickname
    ) {

    }

    public record CommentReplyAddedContext(
            Long crewId,
            String crewName,
            Long squadId,
            String squadTitle,
            Long parentCommentId,
            Long parentCommentWriterId,
            Long replyCommentId,
            Long replyCommentWriterId,
            String replyCommentWriterNickname
    ) {

    }
}
