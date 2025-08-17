package revi1337.onsquad.squad_comment.presentation.dto.response;

import java.time.LocalDateTime;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_comment.domain.dto.SimpleSquadCommentDto;

public record SimpleSquadCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberResponse memberInfo
) {
    public static SimpleSquadCommentResponse from(SimpleSquadCommentDto simpleSquadCommentDto) {
        return new SimpleSquadCommentResponse(
                simpleSquadCommentDto.id(),
                simpleSquadCommentDto.content(),
                simpleSquadCommentDto.createdAt(),
                simpleSquadCommentDto.updatedAt(),
                SimpleMemberResponse.from(simpleSquadCommentDto.memberInfo())
        );
    }
}
