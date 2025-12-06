package revi1337.onsquad.squad_participant.application.dto;

import java.util.List;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;
import revi1337.onsquad.squad_participant.domain.dto.SquadParticipantRequest;

public record SquadParticipantRequestDto(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberDto crewOwner,
        List<SquadParticipantDto> squads
) {

    public static SquadParticipantRequestDto from(SquadParticipantRequest squadParticipantRequest) {
        return new SquadParticipantRequestDto(
                squadParticipantRequest.crewId(),
                squadParticipantRequest.crewName().getValue(),
                squadParticipantRequest.imageUrl(),
                SimpleMemberDto.from(squadParticipantRequest.crewOwner()),
                squadParticipantRequest.squads().stream()
                        .map(SquadParticipantDto::from)
                        .toList()
        );
    }
}
