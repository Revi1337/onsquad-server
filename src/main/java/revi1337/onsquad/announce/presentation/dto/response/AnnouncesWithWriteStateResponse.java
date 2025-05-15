package revi1337.onsquad.announce.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.announce.application.dto.AnnouncesWithWriteStateDto;

public record AnnouncesWithWriteStateResponse(
        boolean canWrite,
        List<AnnounceResponse> announces
) {
    public static AnnouncesWithWriteStateResponse from(AnnouncesWithWriteStateDto announcesWithWriteStateDto) {
        return new AnnouncesWithWriteStateResponse(
                announcesWithWriteStateDto.canWrite(),
                announcesWithWriteStateDto.announces().stream()
                        .map(AnnounceResponse::from)
                        .toList()
        );
    }
}
