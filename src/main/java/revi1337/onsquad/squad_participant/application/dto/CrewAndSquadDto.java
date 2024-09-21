package revi1337.onsquad.squad_participant.application.dto;

import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;
import revi1337.onsquad.squad_participant.domain.dto.CrewAndSquadDomainDto;

public record CrewAndSquadDto(
        Long crewId,
        String crewName,
        SimpleMemberInfoDto crewOwner,
        SquadInfoDto squadInfo
) {
    public static CrewAndSquadDto from(CrewAndSquadDomainDto crewAndSquadDomainDto) {
        return new CrewAndSquadDto(
                crewAndSquadDomainDto.crewId(),
                crewAndSquadDomainDto.crewName().getValue(),
                SimpleMemberInfoDto.from(crewAndSquadDomainDto.crewOwner()),
                SquadInfoDto.from(crewAndSquadDomainDto.squadInfo())
        );
    }
}
