package revi1337.onsquad.crew_comment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.crew_comment.application.dto.CrewCommentDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewCommentResponse(
        Long parentId,
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse memberInfo,
        List<CrewCommentResponse> replies
) {
    public static CrewCommentResponse from(CrewCommentDto crewCommentDto) {
        return new CrewCommentResponse(
                crewCommentDto.parentCommentId(),
                crewCommentDto.commentId(),
                crewCommentDto.content(),
                crewCommentDto.createdAt(),
                crewCommentDto.updatedAt(),
                SimpleMemberInfoResponse.from(crewCommentDto.memberInfo()),
                crewCommentDto.replies().stream()
                        .map(CrewCommentResponse::from)
                        .toList()
        );
    }
}