package revi1337.onsquad.crew_participant.presentation.dto.response;

import revi1337.onsquad.crew_participant.application.dto.CrewParticipantDto;

import java.time.LocalDateTime;

public record CrewParticipantResponse(
        Long id,
        LocalDateTime requestAt
) {
    public static CrewParticipantResponse from(CrewParticipantDto crewParticipantDto) {
        return new CrewParticipantResponse(
                crewParticipantDto.id(),
                crewParticipantDto.requestAt()
        );
    }
}
