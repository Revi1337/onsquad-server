package revi1337.onsquad.squad_comment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SquadCommentResponse(
        Long parentId,
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse memberInfo,
        List<SquadCommentResponse> replies
) {
    public static SquadCommentResponse from(SquadCommentDto squadCommentDto) {
        return new SquadCommentResponse(
                squadCommentDto.parentCommentId(),
                squadCommentDto.commentId(),
                squadCommentDto.content(),
                squadCommentDto.createdAt(),
                squadCommentDto.updatedAt(),
                SimpleMemberInfoResponse.from(squadCommentDto.memberInfo()),
                squadCommentDto.replies().stream()
                        .map(SquadCommentResponse::from)
                        .toList()
        );
    }
}