package revi1337.onsquad.crew_participant.application.dto;

import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantDomainDto;

import java.time.LocalDateTime;

public record CrewParticipantDto(
        Long id,
        LocalDateTime requestAt
) {
    public static CrewParticipantDto from(CrewParticipantDomainDto crewParticipantDomainDto) {
        return new CrewParticipantDto(
                crewParticipantDomainDto.id(),
                crewParticipantDomainDto.requestAt()
        );
    }
}
