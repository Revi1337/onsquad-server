package revi1337.onsquad.crew_comment.application.dto;

public record CreateCrewCommentReplyDto(
        Long parentCommentId,
        String content
) {
}
