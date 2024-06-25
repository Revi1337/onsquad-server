package revi1337.onsquad.comment.dto.response;

import revi1337.onsquad.comment.dto.CommentDto;
import revi1337.onsquad.member.dto.response.MemberInfoResponse;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        MemberInfoResponse memberInfo
) {
    public static CommentResponse from(CommentDto commentDto) {
        return new CommentResponse(
                commentDto.id(),
                commentDto.comment(),
                commentDto.createdAt(),
                commentDto.updatedAt(),
                MemberInfoResponse.from(commentDto.memberInfo())
        );
    }
}
