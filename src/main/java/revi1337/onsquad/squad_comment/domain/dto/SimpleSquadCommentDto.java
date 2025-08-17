package revi1337.onsquad.squad_comment.domain.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.squad_comment.domain.SquadComment;

public record SimpleSquadCommentDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberDto memberInfo
) {
    public static SimpleSquadCommentDto from(SquadComment squadComment, Member member) {
        return new SimpleSquadCommentDto(
                squadComment.getId(),
                squadComment.getContent(),
                squadComment.getCreatedAt(),
                squadComment.getUpdatedAt(),
                SimpleMemberDto.from(member)
        );
    }
}
