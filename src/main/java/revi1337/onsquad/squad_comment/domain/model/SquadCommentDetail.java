package revi1337.onsquad.squad_comment.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.member.domain.model.SimpleMember;

public record SquadCommentDetail(
        Long parentId,
        Long id,
        String content,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMember writer,
        List<SquadCommentDetail> replies
) {

    public SquadCommentDetail(
            Long parentId,
            Long commentId,
            String content,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            SimpleMember memberInfo
    ) {
        this(parentId, commentId, content, deleted, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    public SquadCommentDetail(
            Long commentId,
            String content,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            SimpleMember memberInfo
    ) {
        this(null, commentId, content, deleted, createdAt, updatedAt, memberInfo, new ArrayList<>());
    }

    public void addReply(SquadCommentDetail reply) {
        this.replies.add(reply);
    }

    public void addReplies(List<SquadCommentDetail> replies) {
        this.replies.addAll(replies);
    }
}
