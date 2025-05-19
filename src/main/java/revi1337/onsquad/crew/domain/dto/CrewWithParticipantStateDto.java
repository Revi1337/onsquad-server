package revi1337.onsquad.crew.domain.dto;

import revi1337.onsquad.crew.application.dto.CrewDto;

public record CrewWithParticipantStateDto(
        boolean alreadyParticipant,
        CrewDto crew
) {
    public static CrewWithParticipantStateDto from(boolean alreadyParticipant, CrewDomainDto domainDto) {
        return new CrewWithParticipantStateDto(
                alreadyParticipant,
                CrewDto.from(domainDto)
        );
    }
}
