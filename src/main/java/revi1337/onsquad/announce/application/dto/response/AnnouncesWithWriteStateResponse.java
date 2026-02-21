package revi1337.onsquad.announce.application.dto.response;

import java.util.List;

public record AnnouncesWithWriteStateResponse(
        AnnounceStates states,
        List<AnnounceResponse> announces
) {

    public static AnnouncesWithWriteStateResponse from(boolean canWrite, List<AnnounceResponse> details) {
        return new AnnouncesWithWriteStateResponse(new AnnounceStates(canWrite), details);
    }
}
