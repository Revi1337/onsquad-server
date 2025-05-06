package revi1337.onsquad.announce.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.announce.application.dto.AnnouncesWithCreateStateDto;

public record AnnouncesWithCreateStateResponse(
        boolean canCreate,
        List<AnnounceResponse> announces
) {
    public static AnnouncesWithCreateStateResponse from(AnnouncesWithCreateStateDto announcesWithCreateStateDto) {
        return new AnnouncesWithCreateStateResponse(
                announcesWithCreateStateDto.canCreate(),
                announcesWithCreateStateDto.announces().stream()
                        .map(AnnounceResponse::from)
                        .toList()
        );
    }
}
