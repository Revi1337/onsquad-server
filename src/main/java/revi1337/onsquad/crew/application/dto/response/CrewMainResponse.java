package revi1337.onsquad.crew.application.dto.response;

import java.util.List;
import revi1337.onsquad.announce.application.dto.response.AnnounceResponse;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.crew.domain.model.CrewDetail;
import revi1337.onsquad.crew_member.application.response.CrewRankedMemberResponse;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.domain.result.SquadResult;

public record CrewMainResponse(
        CrewStates states,
        CrewResponse crew,
        List<AnnounceResponse> announces,
        List<CrewRankedMemberResponse> topMembers,
        List<SquadResponse> squads
) {

    public static CrewMainResponse from(
            boolean canManage,
            CrewDetail result,
            List<AnnounceResult> announces,
            List<CrewRankedMember> topMembers,
            List<SquadResult> squads
    ) {
        return new CrewMainResponse(
                new CrewStates(canManage),
                CrewResponse.from(result),
                announces.stream()
                        .map(AnnounceResponse::from)
                        .toList(),
                topMembers.stream()
                        .map(CrewRankedMemberResponse::from)
                        .toList(),
                squads.stream()
                        .map(SquadResponse::from)
                        .toList()
        );
    }
}
