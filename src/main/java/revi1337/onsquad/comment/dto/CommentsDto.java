package revi1337.onsquad.comment.dto;

import revi1337.onsquad.comment.domain.Comment;
import revi1337.onsquad.member.dto.MemberInfoDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record CommentsDto(
        Long parentCommentId,
        Long commentId,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        MemberInfoDto memberInfo,
        List<CommentsDto> replies
) {
    public static CommentsDto from(Comment comment) {
        return new CommentsDto(
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                MemberInfoDto.from(comment.getMember()),
                comment.getReplies().stream()
                        .map(CommentsDto::from)
                        .collect(Collectors.toList())
        );
    }
}