package revi1337.onsquad.crew_comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.crew_comment.dto.CreateCrewCommentDto;

public record CreateCrewCommentRequest(
        @NotEmpty String content
) {
    public CreateCrewCommentDto toDto() {
        return new CreateCrewCommentDto(content);
    }
}
