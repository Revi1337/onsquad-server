package revi1337.onsquad.crew.domain.result;

import com.querydsl.core.annotations.QueryProjection;

public record CrewWithOwnerStateResult(
        Boolean isOwner,
        SimpleCrewResult crew
) {

    @QueryProjection
    public CrewWithOwnerStateResult(Boolean isOwner, SimpleCrewResult crew) {
        this.isOwner = isOwner;
        this.crew = crew;
    }
}
