package revi1337.onsquad.crew_participant.presentation.dto.response;

import revi1337.onsquad.crew_participant.application.dto.CrewParticipantRequestDto;
import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;

public record CrewParticipantRequestResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberInfoResponse crewOwner,
        CrewParticipantResponse request
) {
    public static CrewParticipantRequestResponse from(CrewParticipantRequestDto crewParticipantRequest) {
        return new CrewParticipantRequestResponse(
                crewParticipantRequest.crewId(),
                crewParticipantRequest.crewName(),
                crewParticipantRequest.imageUrl(),
                SimpleMemberInfoResponse.from(crewParticipantRequest.crewOwner()),
                CrewParticipantResponse.from(crewParticipantRequest.request())
        );
    }
}
