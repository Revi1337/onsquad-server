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
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse writer,
        List<SquadCommentResponse> replies
) {
    public static SquadCommentResponse from(SquadCommentDto squadCommentDto) {
        return new SquadCommentResponse(
                squadCommentDto.parentId(),
                squadCommentDto.commentId(),
                squadCommentDto.content(),
                squadCommentDto.createdAt(),
                squadCommentDto.updatedAt(),
                SimpleMemberInfoResponse.from(squadCommentDto.writer()),
                squadCommentDto.replies().stream()
                        .map(SquadCommentResponse::from)
                        .toList()
        );
    }
}