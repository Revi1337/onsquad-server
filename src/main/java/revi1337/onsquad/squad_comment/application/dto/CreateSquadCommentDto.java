package revi1337.onsquad.squad_comment.application.dto;

public record CreateSquadCommentDto(
        Long parentId,
        String content
) {
}
