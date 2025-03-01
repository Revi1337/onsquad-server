package revi1337.onsquad.squad_participant.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantDto;

public record SquadParticipantResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberInfoResponse owner,
        RequestParticipantResponse request
) {
    public static SquadParticipantResponse from(SquadParticipantDto squadParticipantDto) {
        return new SquadParticipantResponse(
                squadParticipantDto.id(),
                squadParticipantDto.title(),
                squadParticipantDto.capacity(),
                squadParticipantDto.remain(),
                squadParticipantDto.categories(),
                SimpleMemberInfoResponse.from(squadParticipantDto.squadOwner()),
                RequestParticipantResponse.from(squadParticipantDto.request())
        );
    }

    public record RequestParticipantResponse(
            Long id,
            LocalDateTime requestAt
    ) {
        public static RequestParticipantResponse from(SquadParticipantDto.RequestParticipantDto requestParticipantDto) {
            return new RequestParticipantResponse(
                    requestParticipantDto.id(),
                    requestParticipantDto.requestAt()
            );
        }
    }
}
