package revi1337.onsquad.announce.application.dto.response;

import java.util.List;
import revi1337.onsquad.announce.domain.result.AnnounceResult;

public record AnnouncesWithWriteStateResponse(
        boolean canWrite,
        List<AnnounceResponse> announces
) {

    public static AnnouncesWithWriteStateResponse from(Boolean canWrite, List<AnnounceResult> announceDtos) {
        return new AnnouncesWithWriteStateResponse(
                canWrite,
                announceDtos.stream()
                        .map(AnnounceResponse::from)
                        .toList()
        );
    }
}
