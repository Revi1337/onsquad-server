package revi1337.onsquad.crew.application.dto.response;

import java.util.List;
import revi1337.onsquad.announce.application.dto.response.AnnounceResponse;
import revi1337.onsquad.announce.domain.model.AnnounceDetail;
import revi1337.onsquad.crew.domain.model.CrewDetail;
import revi1337.onsquad.crew_member.application.response.CrewRankedMemberResponse;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.domain.model.SquadDetail;

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
            List<AnnounceDetail> announces,
            List<CrewRankedMember> topMembers,
            List<SquadDetail> squads
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
