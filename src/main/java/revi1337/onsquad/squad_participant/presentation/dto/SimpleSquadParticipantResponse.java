package revi1337.onsquad.squad_participant.presentation.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_participant.application.dto.SimpleSquadParticipantDto;

public record SimpleSquadParticipantResponse(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberResponse member
) {
    public static SimpleSquadParticipantResponse from(SimpleSquadParticipantDto simpleSquadParticipantDto) {
        return new SimpleSquadParticipantResponse(
                simpleSquadParticipantDto.id(),
                simpleSquadParticipantDto.requestAt(),
                SimpleMemberResponse.from(simpleSquadParticipantDto.member())
        );
    }
}
