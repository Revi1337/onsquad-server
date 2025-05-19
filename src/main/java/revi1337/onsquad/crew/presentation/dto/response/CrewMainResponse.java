package revi1337.onsquad.crew.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.announce.presentation.dto.response.AnnounceResponse;
import revi1337.onsquad.backup.crew.presentation.dto.Top5CrewMemberResponse;
import revi1337.onsquad.crew.application.dto.CrewMainDto;
import revi1337.onsquad.squad.presentation.dto.response.SquadResponse;

public record CrewMainResponse(
        boolean canManage,
        CrewResponse crew,
        List<AnnounceResponse> announces,
        List<Top5CrewMemberResponse> topMembers,
        List<SquadResponse> squads
) {
    public static CrewMainResponse from(CrewMainDto crewMainDto) {
        return new CrewMainResponse(
                crewMainDto.canManage(),
                CrewResponse.from(crewMainDto.crew()),
                crewMainDto.announces().stream()
                        .map(AnnounceResponse::from)
                        .toList(),
                crewMainDto.topMembers().stream()
                        .map(Top5CrewMemberResponse::from)
                        .toList(),
                crewMainDto.squads().stream()
                        .map(SquadResponse::from)
                        .toList()
        );
    }
}
