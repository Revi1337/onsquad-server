package revi1337.onsquad.crew.presentation.dto.response;

import java.util.List;
import revi1337.onsquad.announce.presentation.dto.response.AnnounceInfoResponse;
import revi1337.onsquad.crew.application.dto.CrewMainDto;
import revi1337.onsquad.crew_member.presentation.dto.response.Top5CrewMemberResponse;
import revi1337.onsquad.squad.presentation.dto.response.SquadInfoResponse;

public record CrewMainResponse(
        CrewInfoResponse crew,
        List<AnnounceInfoResponse> announces,
        List<Top5CrewMemberResponse> topMembers,
        List<SquadInfoResponse> squads
) {
    public static CrewMainResponse from(CrewMainDto crewMainDto) {
        return new CrewMainResponse(
                CrewInfoResponse.from(crewMainDto.crew()),
                crewMainDto.announces().stream()
                        .map(AnnounceInfoResponse::from)
                        .toList(),
                crewMainDto.topMembers().stream()
                        .map(Top5CrewMemberResponse::from)
                        .toList(),
                crewMainDto.squads().stream()
                        .map(SquadInfoResponse::from)
                        .toList()
        );
    }
}
