package revi1337.onsquad.crew.application.dto.response;

import java.util.List;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithRoleStateResponse;
import revi1337.onsquad.crew.domain.model.CrewDetail;
import revi1337.onsquad.crew_member.application.response.CrewRankerResponse;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.domain.model.SquadDetail;

public record CrewMainResponse(
        CrewStates states,
        CrewResponse crew,
        List<AnnounceWithRoleStateResponse> announces,
        List<CrewRankerResponse> rankers,
        List<SquadResponse> squads
) {

    public static CrewMainResponse from(
            boolean canManage,
            CrewDetail result,
            List<AnnounceWithRoleStateResponse> announces,
            List<CrewRankerResponse> topMembers,
            List<SquadDetail> squads
    ) {
        return new CrewMainResponse(
                new CrewStates(canManage),
                CrewResponse.from(result),
                announces,
                topMembers,
                squads.stream()
                        .map(SquadResponse::from)
                        .toList()
        );
    }
}
