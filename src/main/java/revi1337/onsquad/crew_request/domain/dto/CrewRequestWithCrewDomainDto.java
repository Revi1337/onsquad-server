package revi1337.onsquad.crew_request.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.dto.SimpleCrewDomainDto;

public record CrewRequestWithCrewDomainDto(
        SimpleCrewDomainDto crew,
        CrewRequestDomainDto request
) {

    @QueryProjection
    public CrewRequestWithCrewDomainDto(
            SimpleCrewDomainDto crew,
            CrewRequestDomainDto request
    ) {
        this.crew = crew;
        this.request = request;
    }
}
