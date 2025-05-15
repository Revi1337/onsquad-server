package revi1337.onsquad.announce.presentation.dto.response;

import revi1337.onsquad.announce.application.dto.AnnounceWithFixAndModifyStateDto;

public record AnnounceWithFixAndModifyStateResponse(
        boolean canFix,
        boolean canModify,
        AnnounceResponse announce
) {
    public static AnnounceWithFixAndModifyStateResponse from(AnnounceWithFixAndModifyStateDto announceStateDto) {
        return new AnnounceWithFixAndModifyStateResponse(
                announceStateDto.canFix(),
                announceStateDto.canModify(),
                AnnounceResponse.from(announceStateDto.announce())
        );
    }
}
