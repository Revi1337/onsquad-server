package revi1337.onsquad.squad_participant.presentation.dto;

import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad.presentation.dto.response.SquadResponse;
import revi1337.onsquad.squad_participant.application.dto.CrewAndSquadDto;

public record CrewAndSquadResponse(
        Long crewId,
        String crewName,
        SimpleMemberResponse crewOwner,
        SquadResponse squadInfo
) {
    public static CrewAndSquadResponse from(CrewAndSquadDto crewAndSquadDto) {
        return new CrewAndSquadResponse(
                crewAndSquadDto.crewId(),
                crewAndSquadDto.crewName(),
                SimpleMemberResponse.from(crewAndSquadDto.crewOwner()),
                SquadResponse.from(crewAndSquadDto.squadInfo())
        );
    }
}

