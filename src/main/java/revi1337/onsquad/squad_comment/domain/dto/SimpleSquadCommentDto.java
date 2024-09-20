package revi1337.onsquad.squad_comment.domain.dto;

import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_comment.domain.SquadComment;

import java.time.LocalDateTime;

public record SimpleSquadCommentDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDto memberInfo
) {
    public static SimpleSquadCommentDto from(SquadComment squadComment, Member member) {
        return new SimpleSquadCommentDto(
                squadComment.getId(),
                squadComment.getContent(),
                squadComment.getCreatedAt(),
                squadComment.getUpdatedAt(),
                SimpleMemberInfoDto.from(member)
        );
    }
}
