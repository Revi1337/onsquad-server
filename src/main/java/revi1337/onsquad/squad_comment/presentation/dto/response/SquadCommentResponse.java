package revi1337.onsquad.squad_comment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadCommentResponse(
        Long parentId,
        Long id,
        String content,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse writer,
        List<SquadCommentResponse> replies
) {
    public static SquadCommentResponse from(SquadCommentDto dto) {
        return new SquadCommentResponse(
                dto.parentId(),
                dto.commentId(),
                dto.content(),
                dto.deleted(),
                dto.createdAt(),
                dto.updatedAt(),
                dto.writer() != null ? SimpleMemberInfoResponse.from(dto.writer()) : null,
                dto.replies().stream()
                        .map(SquadCommentResponse::from)
                        .toList()
        );
    }
}
