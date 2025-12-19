package revi1337.onsquad.announce.domain.result;

import com.querydsl.core.annotations.QueryProjection;

public record AnnounceWithModifyStateResult(
        Boolean canModify,
        AnnounceResult announce
) {

    @QueryProjection
    public AnnounceWithModifyStateResult(Boolean canModify, AnnounceResult announce) {
        this.canModify = canModify;
        this.announce = announce;
    }
}
