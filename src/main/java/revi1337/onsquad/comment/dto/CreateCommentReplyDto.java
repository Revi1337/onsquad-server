package revi1337.onsquad.comment.dto;

public record CreateCommentReplyDto(
        String crewName,
        Long parentCommentId,
        String content
) {
}
