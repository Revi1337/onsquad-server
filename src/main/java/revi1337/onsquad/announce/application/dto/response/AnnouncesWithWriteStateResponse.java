package revi1337.onsquad.announce.application.dto.response;

import java.util.List;

public record AnnouncesWithWriteStateResponse(
        AnnounceStates states,
        List<AnnounceWithRoleStateResponse> announces
) {

    public static AnnouncesWithWriteStateResponse from(boolean canWrite, List<AnnounceWithRoleStateResponse> details) {
        return new AnnouncesWithWriteStateResponse(new AnnounceStates(canWrite), details);
    }
}
