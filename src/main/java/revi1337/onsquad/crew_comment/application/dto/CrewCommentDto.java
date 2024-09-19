package revi1337.onsquad.crew_comment.application.dto;

import revi1337.onsquad.crew_comment.domain.dto.CrewCommentDomainDto;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

import java.time.LocalDateTime;
import java.util.List;

public record CrewCommentDto(
        Long parentCommentId,
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDto memberInfo,
        List<CrewCommentDto> replies
) {
    public static CrewCommentDto from(CrewCommentDomainDto crewCommentDomainDto) {
        return new CrewCommentDto(
                crewCommentDomainDto.parentCommentId(),
                crewCommentDomainDto.commentId(),
                crewCommentDomainDto.content(),
                crewCommentDomainDto.createdAt(),
                crewCommentDomainDto.updatedAt(),
                SimpleMemberInfoDto.from(crewCommentDomainDto.memberInfo()),
                crewCommentDomainDto.replies().stream()
                        .map(CrewCommentDto::from)
                        .toList()
        );
    }
}