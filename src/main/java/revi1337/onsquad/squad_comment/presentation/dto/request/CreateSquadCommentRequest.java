package revi1337.onsquad.squad_comment.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.squad_comment.application.dto.CreateSquadCommentDto;

public record CreateSquadCommentRequest(
        @Positive Long parentId,
        @NotEmpty String content
) {
    public CreateSquadCommentDto toDto() {
        return new CreateSquadCommentDto(parentId, content);
    }
}
