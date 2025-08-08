package revi1337.onsquad.squad_comment.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SquadCommentDomainDto(
        Long parentId,
        Long id,
        String content,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberDomainDto writer,
        List<SquadCommentDomainDto> replies
) {
    @QueryProjection
    public SquadCommentDomainDto(Long parentId, Long commentId, String content, boolean deleted, LocalDateTime createdAt,
                                 LocalDateTime updatedAt, SimpleMemberDomainDto memberInfo) {
        this(parentId, commentId, content, deleted, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    @QueryProjection
    public SquadCommentDomainDto(Long commentId, String content, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt,
                                 SimpleMemberDomainDto memberInfo) {
        this(null, commentId, content, deleted, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    public void addReply(SquadCommentDomainDto reply) {
        this.replies.add(reply);
    }

    public void addReplies(List<SquadCommentDomainDto> replies) {
        this.replies.addAll(replies);
    }
}
