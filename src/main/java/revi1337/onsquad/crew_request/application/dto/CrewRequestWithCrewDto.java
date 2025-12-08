package revi1337.onsquad.crew_request.application.dto;

import revi1337.onsquad.crew.application.dto.SimpleCrewDto;
import revi1337.onsquad.crew_request.domain.dto.CrewRequestWithCrewDomainDto;

public record CrewRequestWithCrewDto(
        CrewRequestDto request,
        SimpleCrewDto crew
) {

    public static CrewRequestWithCrewDto from(CrewRequestWithCrewDomainDto crewRequestWithCrewDomainDto) {
        return new CrewRequestWithCrewDto(
                CrewRequestDto.from(crewRequestWithCrewDomainDto.request()),
                SimpleCrewDto.from(crewRequestWithCrewDomainDto.crew())
        );
    }
}
