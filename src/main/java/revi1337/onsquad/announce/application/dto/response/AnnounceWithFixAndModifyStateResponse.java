package revi1337.onsquad.announce.application.dto.response;

import revi1337.onsquad.announce.domain.result.AnnounceResult;

public record AnnounceWithFixAndModifyStateResponse(
        boolean canFix,
        boolean canModify,
        AnnounceResponse announce
) {

    public static AnnounceWithFixAndModifyStateResponse from(boolean canFix, boolean canModify, AnnounceResult result) {
        return new AnnounceWithFixAndModifyStateResponse(
                canFix,
                canModify,
                AnnounceResponse.from(result)
        );
    }
}
