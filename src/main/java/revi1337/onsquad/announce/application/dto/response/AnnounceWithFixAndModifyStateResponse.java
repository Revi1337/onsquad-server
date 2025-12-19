package revi1337.onsquad.announce.application.dto.response;

import revi1337.onsquad.announce.domain.result.AnnounceResult;

public record AnnounceWithFixAndModifyStateResponse(
        Boolean canFix,
        Boolean canModify,
        AnnounceResponse announce
) {

    public static AnnounceWithFixAndModifyStateResponse from(Boolean canFix, Boolean canModify, AnnounceResult domainDto) {
        return new AnnounceWithFixAndModifyStateResponse(
                canFix,
                canModify,
                AnnounceResponse.from(domainDto)
        );
    }
}
