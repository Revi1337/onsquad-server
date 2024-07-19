package revi1337.onsquad.comment.dto;

public record CreateCommentReplyDto(
        Long parentCommentId,
        String content
) {
}
