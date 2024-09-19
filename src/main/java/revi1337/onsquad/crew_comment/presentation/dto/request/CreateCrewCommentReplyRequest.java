package revi1337.onsquad.crew_comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.crew_comment.dto.CreateCrewCommentReplyDto;

public record CreateCrewCommentReplyRequest(
        @NotNull @Positive Long parentCommentId,
        @NotEmpty String content
) {
    public CreateCrewCommentReplyDto toDto() {
        return new CreateCrewCommentReplyDto(parentCommentId, content);
    }
}
