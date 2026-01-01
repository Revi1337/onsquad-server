package revi1337.onsquad.announce.application.dto.response;

import java.util.List;
import revi1337.onsquad.announce.domain.result.AnnounceResult;

public record AnnouncesWithWriteStateResponse(
        boolean canWrite,
        List<AnnounceResponse> announces
) {

    public static AnnouncesWithWriteStateResponse from(Boolean canWrite, List<AnnounceResult> results) {
        return new AnnouncesWithWriteStateResponse(
                canWrite,
                results.stream()
                        .map(AnnounceResponse::from)
                        .toList()
        );
    }
}
