package revi1337.onsquad.crew_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.result.SimpleCrewResult;

public record MyParticipantCrewResult(
        Boolean isOwner,
        SimpleCrewResult crew
) {

    @QueryProjection
    public MyParticipantCrewResult(Boolean isOwner, SimpleCrewResult crew) {
        this.isOwner = isOwner;
        this.crew = crew;
    }
}
