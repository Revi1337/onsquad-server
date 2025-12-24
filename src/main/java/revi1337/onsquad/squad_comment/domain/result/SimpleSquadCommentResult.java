package revi1337.onsquad.squad_comment.domain.result;

import java.time.LocalDateTime;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

public record SimpleSquadCommentResult(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SimpleMemberResponse memberInfo
) {

    public static SimpleSquadCommentResult from(SquadComment squadComment, Member member) {
        return new SimpleSquadCommentResult(
                squadComment.getId(),
                squadComment.getContent(),
                squadComment.getCreatedAt(),
                squadComment.getUpdatedAt(),
                SimpleMemberResponse.from(member)
        );
    }
}
