package revi1337.onsquad.announce.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public record AnnounceWithModifyStateDomainDto(
        Boolean canModify,
        AnnounceDomainDto announce
) {
    @QueryProjection
    public AnnounceWithModifyStateDomainDto(Boolean canModify, AnnounceDomainDto announce) {
        this.canModify = canModify;
        this.announce = announce;
    }
}
