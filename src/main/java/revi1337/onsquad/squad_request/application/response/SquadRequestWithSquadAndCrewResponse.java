package revi1337.onsquad.squad_request.application.response;

import java.util.List;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_request.domain.result.SquadRequestWithSquadAndCrewResult;

public record SquadRequestWithSquadAndCrewResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberDto crewOwner,
        List<SquadRequestWithSquadResponse> squads
) {

    public static SquadRequestWithSquadAndCrewResponse from(SquadRequestWithSquadAndCrewResult squadRequestWithSquadAndCrewResult) {
        return new SquadRequestWithSquadAndCrewResponse(
                squadRequestWithSquadAndCrewResult.crewId(),
                squadRequestWithSquadAndCrewResult.crewName().getValue(),
                squadRequestWithSquadAndCrewResult.imageUrl(),
                SimpleMemberDto.from(squadRequestWithSquadAndCrewResult.crewOwner()),
                squadRequestWithSquadAndCrewResult.squads().stream()
                        .map(SquadRequestWithSquadResponse::from)
                        .toList()
        );
    }
}
