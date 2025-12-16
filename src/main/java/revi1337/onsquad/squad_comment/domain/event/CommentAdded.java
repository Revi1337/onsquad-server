package revi1337.onsquad.squad_comment.domain.event;

public record CommentAdded(
        Long writerId,
        Long commentId
) {

}
