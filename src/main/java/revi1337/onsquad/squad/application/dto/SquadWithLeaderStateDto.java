package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.squad.domain.dto.SquadWithLeaderStateDomainDto;

public record SquadWithLeaderStateDto(
        boolean isLeader,
        SimpleSquadDto squad
) {
    public static SquadWithLeaderStateDto from(SquadWithLeaderStateDomainDto domainDto) {
        return new SquadWithLeaderStateDto(
                domainDto.isLeader(),
                SimpleSquadDto.from(domainDto.squad())
        );
    }
}
