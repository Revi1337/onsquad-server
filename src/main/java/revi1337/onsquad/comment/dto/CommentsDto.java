package revi1337.onsquad.comment.dto;

import revi1337.onsquad.comment.domain.Comment;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CommentsDto(
        Long parentCommentId,
        Long commentId,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDto memberInfo,
        List<CommentsDto> replies
) {
    public CommentsDto(Long parentCommentId, Long commentId, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, SimpleMemberInfoDto memberInfo) {
        this(parentCommentId, commentId, comment, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    public static CommentsDto from(Comment comment) {
        return new CommentsDto(
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                SimpleMemberInfoDto.from(comment.getMember())
        );
    }
}