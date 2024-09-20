package revi1337.onsquad.squad_comment.presentation.dto.response;

import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_comment.domain.dto.SimpleSquadCommentDto;

import java.time.LocalDateTime;

public record SimpleSquadCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoResponse memberInfo
) {
    public static SimpleSquadCommentResponse from(SimpleSquadCommentDto simpleSquadCommentDto) {
        return new SimpleSquadCommentResponse(
                simpleSquadCommentDto.id(),
                simpleSquadCommentDto.content(),
                simpleSquadCommentDto.createdAt(),
                simpleSquadCommentDto.updatedAt(),
                SimpleMemberInfoResponse.from(simpleSquadCommentDto.memberInfo())
        );
    }
}
