package revi1337.onsquad.squad_comment.domain.event;

public record CommentReplyAdded(
        Long parentId,
        Long writerId,
        Long replyId
) {

}
