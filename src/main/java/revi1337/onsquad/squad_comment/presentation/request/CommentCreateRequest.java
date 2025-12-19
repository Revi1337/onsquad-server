package revi1337.onsquad.squad_comment.presentation.request;

import jakarta.validation.constraints.NotEmpty;

public record CommentCreateRequest(
        @NotEmpty String content
) {

}
