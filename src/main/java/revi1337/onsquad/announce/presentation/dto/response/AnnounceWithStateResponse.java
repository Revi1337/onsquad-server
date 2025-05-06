package revi1337.onsquad.announce.presentation.dto.response;

import revi1337.onsquad.announce.application.dto.AnnounceWithStateDto;

public record AnnounceWithStateResponse(
        boolean canFix,
        boolean canModify,
        AnnounceResponse announce
) {
    public static AnnounceWithStateResponse from(AnnounceWithStateDto announceWithStateDto) {
        return new AnnounceWithStateResponse(
                announceWithStateDto.canFix(),
                announceWithStateDto.canModify(),
                AnnounceResponse.from(announceWithStateDto.announce())
        );
    }
}
