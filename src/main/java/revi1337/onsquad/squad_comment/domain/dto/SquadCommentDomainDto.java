package revi1337.onsquad.squad_comment.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record SquadCommentDomainDto(
        Long parentCommentId,
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDomainDto memberInfo,
        List<SquadCommentDomainDto> replies
) {
    @QueryProjection
    public SquadCommentDomainDto(Long parentCommentId, Long commentId, String content, LocalDateTime createdAt,
                                 LocalDateTime updatedAt, SimpleMemberInfoDomainDto memberInfo) {
        this(parentCommentId, commentId, content, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    @QueryProjection
    public SquadCommentDomainDto(Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                                 SimpleMemberInfoDomainDto memberInfo) {
        this(null, commentId, content, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }
}