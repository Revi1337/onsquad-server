package revi1337.onsquad.crew_comment.dto;

import revi1337.onsquad.crew_comment.domain.CrewComment;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CrewCommentsDto(
        Long parentCommentId,
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDto memberInfo,
        List<CrewCommentsDto> replies
) {
    public CrewCommentsDto(Long parentCommentId, Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt, SimpleMemberInfoDto memberInfo) {
        this(parentCommentId, commentId, content, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    public static CrewCommentsDto from(CrewComment crewComment) {
        return new CrewCommentsDto(
                crewComment.getParent() != null ? crewComment.getParent().getId() : null,
                crewComment.getId(),
                crewComment.getContent(),
                crewComment.getCreatedAt(),
                crewComment.getUpdatedAt(),
                SimpleMemberInfoDto.from(crewComment.getMember())
        );
    }
}