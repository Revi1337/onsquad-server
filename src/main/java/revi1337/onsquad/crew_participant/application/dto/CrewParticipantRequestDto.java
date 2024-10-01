package revi1337.onsquad.crew_participant.application.dto;

import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantRequest;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record CrewParticipantRequestDto(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberInfoDto crewOwner,
        CrewParticipantDto request
) {
    public static CrewParticipantRequestDto from(CrewParticipantRequest crewParticipantRequest) {
        return new CrewParticipantRequestDto(
                crewParticipantRequest.crewId(),
                crewParticipantRequest.crewName().getValue(),
                crewParticipantRequest.imageUrl(),
                SimpleMemberInfoDto.from(crewParticipantRequest.crewOwner()),
                CrewParticipantDto.from(crewParticipantRequest.request())
        );
    }
}
