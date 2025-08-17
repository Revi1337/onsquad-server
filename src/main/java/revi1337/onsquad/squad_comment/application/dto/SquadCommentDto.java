package revi1337.onsquad.squad_comment.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;

public record SquadCommentDto(
        Long parentId,
        Long commentId,
        String content,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberDto writer,
        List<SquadCommentDto> replies
) {
    public static SquadCommentDto from(SquadCommentDomainDto squadCommentDomainDto) {
        return new SquadCommentDto(
                squadCommentDomainDto.parentId(),
                squadCommentDomainDto.id(),
                squadCommentDomainDto.content(),
                squadCommentDomainDto.deleted(),
                squadCommentDomainDto.createdAt(),
                squadCommentDomainDto.updatedAt(),
                squadCommentDomainDto.writer() != null ? SimpleMemberDto.from(squadCommentDomainDto.writer()) : null,
                squadCommentDomainDto.replies().stream()
                        .map(SquadCommentDto::from)
                        .toList()
        );
    }
}
