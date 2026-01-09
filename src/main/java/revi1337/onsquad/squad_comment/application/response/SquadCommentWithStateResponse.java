package revi1337.onsquad.squad_comment.application.response;

public record SquadCommentWithStateResponse(
        boolean canDelete,
        SquadCommentResponse comment
) {

}
