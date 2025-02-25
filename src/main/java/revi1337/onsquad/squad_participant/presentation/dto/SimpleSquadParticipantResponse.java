package revi1337.onsquad.squad_participant.presentation.dto;

import java.time.LocalDateTime;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_participant.application.dto.SimpleSquadParticipantDto;

public record SimpleSquadParticipantResponse(
        SimpleMemberInfoResponse memberInfo,
        LocalDateTime requestAt
) {
    public static SimpleSquadParticipantResponse from(SimpleSquadParticipantDto simpleSquadParticipantDto) {
        return new SimpleSquadParticipantResponse(
                SimpleMemberInfoResponse.from(simpleSquadParticipantDto.memberInfo()),
                simpleSquadParticipantDto.requestAt()
        );
    }
}
