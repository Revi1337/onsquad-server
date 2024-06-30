package revi1337.onsquad.comment.dto.response;

import revi1337.onsquad.comment.dto.CommentDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse memberInfo
) {
    public static CommentResponse from(CommentDto commentDto) {
        return new CommentResponse(
                commentDto.id(),
                commentDto.comment(),
                commentDto.createdAt(),
                commentDto.updatedAt(),
                SimpleMemberInfoResponse.from(commentDto.memberInfo())
        );
    }
}
