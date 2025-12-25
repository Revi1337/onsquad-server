package revi1337.onsquad.crew_request.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.result.SimpleCrewResult;

public record CrewRequestWithCrewResult(
        SimpleCrewResult crew,
        CrewRequestResult request
) {

    @QueryProjection
    public CrewRequestWithCrewResult(SimpleCrewResult crew, CrewRequestResult request) {
        this.crew = crew;
        this.request = request;
    }
}
