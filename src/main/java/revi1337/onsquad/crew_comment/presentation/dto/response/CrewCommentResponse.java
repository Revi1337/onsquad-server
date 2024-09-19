package revi1337.onsquad.crew_comment.dto.response;

import revi1337.onsquad.crew_comment.dto.CrewCommentDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.time.LocalDateTime;

public record CrewCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse memberInfo
) {
    public static CrewCommentResponse from(CrewCommentDto crewCommentDto) {
        return new CrewCommentResponse(
                crewCommentDto.id(),
                crewCommentDto.content(),
                crewCommentDto.createdAt(),
                crewCommentDto.updatedAt(),
                SimpleMemberInfoResponse.from(crewCommentDto.memberInfo())
        );
    }
}
