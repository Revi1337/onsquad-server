package revi1337.onsquad.crew_comment.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.dto.SimpleMemberInfoDomainDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CrewCommentDomainDto(
        Long parentCommentId,
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDomainDto memberInfo,
        List<CrewCommentDomainDto> replies
) {
    @QueryProjection
    public CrewCommentDomainDto(Long parentCommentId, Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt, SimpleMemberInfoDomainDto memberInfo) {
        this(parentCommentId, commentId, content, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    @QueryProjection
    public CrewCommentDomainDto(Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt, SimpleMemberInfoDomainDto memberInfo) {
        this(null, commentId, content, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }
}