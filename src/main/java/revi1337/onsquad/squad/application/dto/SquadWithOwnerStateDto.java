package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.squad.domain.dto.SquadWithOwnerStateDomainDto;

public record SquadWithOwnerStateDto(
        Boolean isOwner,
        SquadDto squad
) {
    public static SquadWithOwnerStateDto from(SquadWithOwnerStateDomainDto domainDto) {
        return new SquadWithOwnerStateDto(
                domainDto.isOwner(),
                SquadDto.from(domainDto.squad())
        );
    }
}
