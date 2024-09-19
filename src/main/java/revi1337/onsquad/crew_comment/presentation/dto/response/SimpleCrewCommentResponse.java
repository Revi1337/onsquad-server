package revi1337.onsquad.crew_comment.presentation.dto.response;

import revi1337.onsquad.crew_comment.domain.dto.SimpleCrewCommentDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

import java.time.LocalDateTime;

public record SimpleCrewCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse memberInfo
) {
    public static SimpleCrewCommentResponse from(SimpleCrewCommentDto simpleCrewCommentDto) {
        return new SimpleCrewCommentResponse(
                simpleCrewCommentDto.id(),
                simpleCrewCommentDto.content(),
                simpleCrewCommentDto.createdAt(),
                simpleCrewCommentDto.updatedAt(),
                SimpleMemberInfoResponse.from(simpleCrewCommentDto.memberInfo())
        );
    }
}
