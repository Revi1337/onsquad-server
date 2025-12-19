package revi1337.onsquad.crew.application.dto.response;

import java.util.List;
import revi1337.onsquad.announce.application.dto.response.AnnounceResponse;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.domain.result.SquadResult;

public record CrewMainResponse(
        boolean canManage,
        CrewResponse crew,
        List<AnnounceResponse> announces,
        List<Top5CrewMemberResponse> topMembers,
        List<SquadResponse> squads
) {

    public static CrewMainResponse from(boolean canManage, CrewResult crew, List<AnnounceResult> announces,
                                        List<CrewTopMember> topMembers, List<SquadResult> squads) {
        return new CrewMainResponse(
                canManage,
                CrewResponse.from(crew),
                announces.stream()
                        .map(AnnounceResponse::from)
                        .toList(),
                topMembers.stream()
                        .map(Top5CrewMemberResponse::from)
                        .toList(),
                squads.stream()
                        .map(SquadResponse::from)
                        .toList()
        );
    }
}
