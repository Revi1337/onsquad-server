package revi1337.onsquad.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.comment.dto.CreateCommentDto;

public record CreateCommentRequest(
        @NotEmpty String content
) {
    public CreateCommentDto toDto() {
        return new CreateCommentDto(content);
    }
}
