package revi1337.onsquad.announce.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.model.AnnounceDetail;
import revi1337.onsquad.crew_member.domain.entity.vo.CrewRole;
import revi1337.onsquad.member.application.dto.response.SimpleMemberResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnnounceWithPinAndModifyStateResponse(
        AnnounceStates states,
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        boolean pinned,
        LocalDateTime pinnedAt,
        SimpleMemberResponse writer
) {

    public static AnnounceWithPinAndModifyStateResponse from(CrewRole role, boolean canPin, boolean canModify, AnnounceDetail detail) {
        return new AnnounceWithPinAndModifyStateResponse(
                new AnnounceStates(role, canPin, canModify),
                detail.id(),
                detail.title().getValue(),
                detail.content(),
                detail.createdAt(),
                detail.pinned(),
                detail.pinnedAt(),
                detail.writer().id() != null ? SimpleMemberResponse.from(detail.writer()) : SimpleMemberResponse.DELETED_MEMBER
        );
    }
}
