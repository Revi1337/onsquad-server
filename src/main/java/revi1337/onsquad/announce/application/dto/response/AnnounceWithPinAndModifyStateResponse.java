package revi1337.onsquad.announce.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
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

    public static AnnounceWithPinAndModifyStateResponse from(boolean canPin, boolean canModify, AnnounceResponse announce) {
        return new AnnounceWithPinAndModifyStateResponse(
                new AnnounceStates(announce.states().role(), canPin, canModify),
                announce.id(),
                announce.title(),
                announce.content(),
                announce.createdAt(),
                announce.pinned(),
                announce.pinnedAt(),
                announce.writer()
        );
    }
}
