package revi1337.onsquad.comment.dto.request;

import revi1337.onsquad.comment.dto.CreateCommentDto;

public record CreateCommentRequest(
        String content
) {
    public CreateCommentDto toDto() {
        return new CreateCommentDto(content);
    }
}
