package revi1337.onsquad.crew_participant.application.dto;

import revi1337.onsquad.crew.application.dto.SimpleCrewInfoDto;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithCrewDomainDto;

public record CrewRequestWithCrewDto(
        CrewRequestDto request,
        SimpleCrewInfoDto crew
) {
    public static CrewRequestWithCrewDto from(CrewRequestWithCrewDomainDto crewRequestWithCrewDomainDto) {
        return new CrewRequestWithCrewDto(
                CrewRequestDto.from(crewRequestWithCrewDomainDto.request()),
                SimpleCrewInfoDto.from(crewRequestWithCrewDomainDto.crew())
        );
    }
}
