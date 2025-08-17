package revi1337.onsquad.squad_participant.presentation.dto;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantRequestDto;

public record SquadParticipantRequestResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberResponse crewOwner,
        List<SquadParticipantResponse> squads
) {
    public static SquadParticipantRequestResponse from(SquadParticipantRequestDto squadParticipantRequestDto) {
        return new SquadParticipantRequestResponse(
                squadParticipantRequestDto.crewId(),
                squadParticipantRequestDto.crewName(),
                squadParticipantRequestDto.imageUrl(),
                SimpleMemberResponse.from(squadParticipantRequestDto.crewOwner()),
                squadParticipantRequestDto.squads().stream()
                        .map(SquadParticipantResponse::from)
                        .toList()
        );
    }
}
