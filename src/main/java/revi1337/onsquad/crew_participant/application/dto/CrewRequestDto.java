package revi1337.onsquad.crew_participant.application.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestDomainDto;

public record CrewRequestDto(
        Long id,
        LocalDateTime requestAt
) {
    public static CrewRequestDto from(CrewRequestDomainDto crewRequestDomainDto) {
        return new CrewRequestDto(
                crewRequestDomainDto.id(),
                crewRequestDomainDto.requestAt()
        );
    }
}
