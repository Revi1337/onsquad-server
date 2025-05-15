package revi1337.onsquad.crew_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.dto.SimpleCrewInfoDomainDto;

public record CrewRequestWithCrewDomainDto(
        SimpleCrewInfoDomainDto crew,
        CrewRequestDomainDto request
) {
    @QueryProjection
    public CrewRequestWithCrewDomainDto(
            SimpleCrewInfoDomainDto crew,
            CrewRequestDomainDto request
    ) {
        this.crew = crew;
        this.request = request;
    }
}
