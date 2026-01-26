package revi1337.onsquad.announce.application.dto.response;

import revi1337.onsquad.announce.domain.result.AnnounceResult;

public record AnnounceWithPinAndModifyStateResponse(
        boolean canPin,
        boolean canModify,
        AnnounceResponse announce
) {

    public static AnnounceWithPinAndModifyStateResponse from(boolean canPin, boolean canModify, AnnounceResult result) {
        return new AnnounceWithPinAndModifyStateResponse(
                canPin,
                canModify,
                AnnounceResponse.from(result)
        );
    }
}
