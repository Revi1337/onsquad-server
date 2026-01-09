package revi1337.onsquad.squad_comment.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadCommentResponse(
        Long parentId,
        Long id,
        boolean deleted,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberResponse writer,
        List<SquadCommentResponse> replies
) {

    private static final String DELETED_CONTENT = "삭제된 댓글입니다.";

    public static SquadCommentResponse from(SquadComment comment) {
        if (comment.isDeleted()) {
            return new SquadCommentResponse(null, null, true, DELETED_CONTENT, null, null, null, new ArrayList<>());
        }
        return new SquadCommentResponse(
                comment.getParent() == null ? null : comment.getParent().getId(),
                comment.getId(),
                false,
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                SimpleMemberResponse.from(comment.getMember()),
                new ArrayList<>()
        );
    }
}
