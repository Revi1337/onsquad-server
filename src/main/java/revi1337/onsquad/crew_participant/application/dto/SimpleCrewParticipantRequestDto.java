package revi1337.onsquad.crew_participant.application.dto;

import revi1337.onsquad.crew_participant.domain.dto.SimpleCrewParticipantRequest;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

public record SimpleCrewParticipantRequestDto(
        SimpleMemberInfoDto memberInfo,
        CrewParticipantDto request
) {
    public static SimpleCrewParticipantRequestDto from(SimpleCrewParticipantRequest simpleCrewParticipantRequest) {
        return new SimpleCrewParticipantRequestDto(
                SimpleMemberInfoDto.from(simpleCrewParticipantRequest.memberInfo()),
                CrewParticipantDto.from(simpleCrewParticipantRequest.request())
        );
    }
}
