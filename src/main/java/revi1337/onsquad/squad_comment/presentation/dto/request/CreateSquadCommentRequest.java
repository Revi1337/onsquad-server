package revi1337.onsquad.squad_comment.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record CreateSquadCommentRequest(
        @NotEmpty String content
) {
}
