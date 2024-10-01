package revi1337.onsquad.announce.presentation.dto.response;

import revi1337.onsquad.announce.domain.dto.AnnounceInfosWithAuthDto;

import java.util.List;

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
