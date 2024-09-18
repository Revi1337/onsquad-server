package revi1337.onsquad.crew_comment.dto;

public record CreateCrewCommentReplyDto(
        Long parentCommentId,
        String content
) {
}
