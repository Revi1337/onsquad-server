package revi1337.onsquad.squad_participant.presentation.dto;

import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad.presentation.dto.response.SquadInfoResponse;
import revi1337.onsquad.squad_participant.application.dto.CrewAndSquadDto;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantRequestDto;

public record CrewAndSquadResponse(
        Long crewId,
        String crewName,
        SimpleMemberInfoResponse crewOwner,
        SquadInfoResponse squadInfo
) {
    public static CrewAndSquadResponse from(CrewAndSquadDto crewAndSquadDto) {
        return new CrewAndSquadResponse(
                crewAndSquadDto.crewId(),
                crewAndSquadDto.crewName(),
                SimpleMemberInfoResponse.from(crewAndSquadDto.crewOwner()),
                SquadInfoResponse.from(crewAndSquadDto.squadInfo())
        );
    }
}

