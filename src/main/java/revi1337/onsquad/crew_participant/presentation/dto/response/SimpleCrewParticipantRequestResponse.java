package revi1337.onsquad.crew_participant.presentation.dto.response;

import revi1337.onsquad.crew_participant.application.dto.SimpleCrewParticipantRequestDto;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;

public record SimpleCrewParticipantRequestResponse(
        SimpleMemberInfoResponse memberInfo,
        CrewParticipantResponse request
) {
    public static SimpleCrewParticipantRequestResponse from(
            SimpleCrewParticipantRequestDto simpleCrewParticipantRequestDto) {
        return new SimpleCrewParticipantRequestResponse(
                SimpleMemberInfoResponse.from(simpleCrewParticipantRequestDto.memberInfo()),
                CrewParticipantResponse.from(simpleCrewParticipantRequestDto.request())
        );
    }
}
