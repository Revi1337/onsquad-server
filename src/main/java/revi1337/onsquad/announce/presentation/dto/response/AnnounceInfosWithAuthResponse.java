package revi1337.onsquad.announce.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.announce.domain.dto.AnnounceInfosWithAuthDto;

public record AnnounceInfosWithAuthResponse(
        boolean hasAuthority,
        List<AnnounceInfoResponse> announces
) {
    public static AnnounceInfosWithAuthResponse from(AnnounceInfosWithAuthDto announceInfosWithAuthDto) {
        return new AnnounceInfosWithAuthResponse(
                announceInfosWithAuthDto.hasAuthority(),
                announceInfosWithAuthDto.announces().stream()
                        .map(AnnounceInfoResponse::from)
                        .toList()
        );
    }
}
