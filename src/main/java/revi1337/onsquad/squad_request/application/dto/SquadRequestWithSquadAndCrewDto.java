package revi1337.onsquad.squad_request.application.dto;

import java.util.List;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_request.domain.dto.SquadRequestWithSquadAndCrewDomainDto;

public record SquadRequestWithSquadAndCrewDto(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberDto crewOwner,
        List<SquadRequestWithSquadDto> squads
) {

    public static SquadRequestWithSquadAndCrewDto from(SquadRequestWithSquadAndCrewDomainDto squadRequestWithSquadAndCrewDomainDto) {
        return new SquadRequestWithSquadAndCrewDto(
                squadRequestWithSquadAndCrewDomainDto.crewId(),
                squadRequestWithSquadAndCrewDomainDto.crewName().getValue(),
                squadRequestWithSquadAndCrewDomainDto.imageUrl(),
                SimpleMemberDto.from(squadRequestWithSquadAndCrewDomainDto.crewOwner()),
                squadRequestWithSquadAndCrewDomainDto.squads().stream()
                        .map(SquadRequestWithSquadDto::from)
                        .toList()
        );
    }
}
