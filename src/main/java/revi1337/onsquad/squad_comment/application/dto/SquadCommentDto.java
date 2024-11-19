package revi1337.onsquad.squad_comment.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

public record SquadCommentDto(
        Long parentCommentId,
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDto memberInfo,
        List<SquadCommentDto> replies
) {
    public static SquadCommentDto from(SquadCommentDomainDto squadCommentDomainDto) {
        return new SquadCommentDto(
                squadCommentDomainDto.parentCommentId(),
                squadCommentDomainDto.commentId(),
                squadCommentDomainDto.content(),
                squadCommentDomainDto.createdAt(),
                squadCommentDomainDto.updatedAt(),
                SimpleMemberInfoDto.from(squadCommentDomainDto.memberInfo()),
                squadCommentDomainDto.replies().stream()
                        .map(SquadCommentDto::from)
                        .toList()
        );
    }
}