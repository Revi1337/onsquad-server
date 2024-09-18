package revi1337.onsquad.crew_comment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import revi1337.onsquad.crew_comment.dto.CrewCommentsDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrewCommentsResponse(
        Long parentCommentId,
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse memberInfo,
        List<CrewCommentsResponse> replies
) {
    public static CrewCommentsResponse from(CrewCommentsDto crewCommentsDto) {
        return new CrewCommentsResponse(
                crewCommentsDto.parentCommentId(),
                crewCommentsDto.commentId(),
                crewCommentsDto.content(),
                crewCommentsDto.createdAt(),
                crewCommentsDto.updatedAt(),
                SimpleMemberInfoResponse.from(crewCommentsDto.memberInfo()),
                crewCommentsDto.replies().stream()
                        .map(CrewCommentsResponse::from)
                        .collect(Collectors.toList())
        );
    }
}