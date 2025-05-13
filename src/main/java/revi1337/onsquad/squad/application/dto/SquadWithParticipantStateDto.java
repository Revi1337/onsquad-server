package revi1337.onsquad.squad.application.dto;

import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;

public record SquadWithParticipantStateDto(
        boolean alreadyParticipant,
        SquadInfoDto squad
) {
    public static SquadWithParticipantStateDto from(boolean alreadyParticipant, SquadInfoDomainDto squadInfoDomainDto) {
        return new SquadWithParticipantStateDto(
                alreadyParticipant,
                SquadInfoDto.from(squadInfoDomainDto)
        );
    }
}
