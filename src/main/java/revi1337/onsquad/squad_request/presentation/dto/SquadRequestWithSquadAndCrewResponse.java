package revi1337.onsquad.squad_request.presentation.dto;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_request.application.dto.SquadRequestWithSquadAndCrewDto;

public record SquadRequestWithSquadAndCrewResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberResponse crewOwner,
        List<SquadRequestWithSquadResponse> squads
) {

    public static SquadRequestWithSquadAndCrewResponse from(SquadRequestWithSquadAndCrewDto squadRequestWithSquadAndCrewDto) {
        return new SquadRequestWithSquadAndCrewResponse(
                squadRequestWithSquadAndCrewDto.crewId(),
                squadRequestWithSquadAndCrewDto.crewName(),
                squadRequestWithSquadAndCrewDto.imageUrl(),
                SimpleMemberResponse.from(squadRequestWithSquadAndCrewDto.crewOwner()),
                squadRequestWithSquadAndCrewDto.squads().stream()
                        .map(SquadRequestWithSquadResponse::from)
                        .toList()
        );
    }
}
