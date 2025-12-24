package revi1337.onsquad.squad_comment.application.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadCommentResponse(
        Long parentId,
        Long commentId,
        String content,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberResponse writer,
        List<SquadCommentResponse> replies
) {

    public static SquadCommentResponse from(SquadCommentResult squadCommentResult) {
        return new SquadCommentResponse(
                squadCommentResult.parentId(),
                squadCommentResult.id(),
                squadCommentResult.content(),
                squadCommentResult.deleted(),
                squadCommentResult.createdAt(),
                squadCommentResult.updatedAt(),
                squadCommentResult.writer() != null ? SimpleMemberResponse.from(squadCommentResult.writer()) : null,
                squadCommentResult.replies().stream()
                        .map(SquadCommentResponse::from)
                        .toList()
        );
    }
}
