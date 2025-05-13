package revi1337.onsquad.crew.domain.dto;

import revi1337.onsquad.crew.application.dto.CrewInfoDto;

public record CrewWithParticipantStateDto(
        boolean alreadyParticipant,
        CrewInfoDto crew
) {
    public static CrewWithParticipantStateDto from(boolean alreadyParticipant, CrewInfoDomainDto domainDto) {
        return new CrewWithParticipantStateDto(
                alreadyParticipant,
                CrewInfoDto.from(domainDto)
        );
    }
}
