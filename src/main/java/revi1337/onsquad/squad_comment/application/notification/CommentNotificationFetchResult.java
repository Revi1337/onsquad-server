package revi1337.onsquad.squad_comment.application.notification;

public class CommentNotificationFetchResult {

    public record CommentAddedNotificationResult(
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

    public record CommentReplyAddedNotificationResult(
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
