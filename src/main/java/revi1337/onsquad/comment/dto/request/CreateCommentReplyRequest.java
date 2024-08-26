package revi1337.onsquad.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.comment.dto.CreateCommentReplyDto;

public record CreateCommentReplyRequest(
        @Positive Long parentCommentId,
        @NotEmpty String content
) {
    public CreateCommentReplyDto toDto() {
        return new CreateCommentReplyDto(parentCommentId, content);
    }
}
