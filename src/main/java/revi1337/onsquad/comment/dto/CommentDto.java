package revi1337.onsquad.comment.dto;

import revi1337.onsquad.comment.domain.Comment;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.dto.MemberInfoDto;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        MemberInfoDto memberInfo
) {
    public static CommentDto from(Comment comment, Member member) {
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                MemberInfoDto.from(member)
        );
    }
}
