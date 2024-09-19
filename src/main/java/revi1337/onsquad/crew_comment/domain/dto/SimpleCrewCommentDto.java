package revi1337.onsquad.crew_comment.domain.dto;

import revi1337.onsquad.crew_comment.domain.CrewComment;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.dto.SimpleMemberInfoDto;

import java.time.LocalDateTime;

public record SimpleCrewCommentDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberInfoDto memberInfo
) {
    public static SimpleCrewCommentDto from(CrewComment crewComment, Member member) {
        return new SimpleCrewCommentDto(
                crewComment.getId(),
                crewComment.getContent(),
                crewComment.getCreatedAt(),
                crewComment.getUpdatedAt(),
                SimpleMemberInfoDto.from(member)
        );
    }
}
