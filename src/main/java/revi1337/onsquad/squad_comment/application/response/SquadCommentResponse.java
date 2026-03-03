package revi1337.onsquad.squad_comment.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_comment.domain.SquadCommentPolicy;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadCommentResponse(
        SquadCommentStates states,
        Long parentId,
        Long id,
        boolean deleted,
        String content,
        LocalDateTime deletedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberResponse writer,
        List<SquadCommentResponse> replies
) {

    public static SquadCommentResponse deleted(boolean canDelete, SquadComment comment) {
        return new SquadCommentResponse(
                new SquadCommentStates(canDelete),
                comment.getParent() == null ? null : comment.getParent().getId(),
                comment.getId(),
                comment.isDeleted(),
                SquadCommentPolicy.DELETED_CONTENT,
                comment.getDeletedAt(),
                null,
                null,
                null,
                new ArrayList<>()
        );
    }

    public static SquadCommentResponse from(boolean canDelete, SquadComment comment) {
        return new SquadCommentResponse(
                new SquadCommentStates(canDelete),
                comment.getParent() == null ? null : comment.getParent().getId(),
                comment.getId(),
                comment.isDeleted(),
                comment.getContent(),
                null,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                SimpleMemberResponse.from(comment.getMember()),
                new ArrayList<>()
        );
    }
}
