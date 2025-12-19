package revi1337.onsquad.squad_comment.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SquadCommentResult(
        Long parentId,
        Long id,
        String content,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberDomainDto writer,
        List<SquadCommentResult> replies
) {

    @QueryProjection
    public SquadCommentResult(Long parentId, Long commentId, String content, boolean deleted, LocalDateTime createdAt,
                              LocalDateTime updatedAt, SimpleMemberDomainDto memberInfo) {
        this(parentId, commentId, content, deleted, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    @QueryProjection
    public SquadCommentResult(Long commentId, String content, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt,
                              SimpleMemberDomainDto memberInfo) {
        this(null, commentId, content, deleted, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    public void addReply(SquadCommentResult reply) {
        this.replies.add(reply);
    }

    public void addReplies(List<SquadCommentResult> replies) {
        this.replies.addAll(replies);
    }
}
