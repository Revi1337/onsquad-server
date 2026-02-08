package revi1337.onsquad.crew_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.model.SimpleCrew;

public record MyParticipantCrewResult(
        Boolean isOwner,
        SimpleCrew crew
) {

    @QueryProjection
    public MyParticipantCrewResult(Boolean isOwner, SimpleCrew crew) {
        this.isOwner = isOwner;
        this.crew = crew;
    }
}
