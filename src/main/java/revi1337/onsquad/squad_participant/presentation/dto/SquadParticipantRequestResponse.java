package revi1337.onsquad.squad_participant.presentation.dto;

import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantRequestDto;

public record SquadParticipantRequestResponse(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberInfoResponse crewOwner,
        List<SquadParticipantResponse> squads
) {
    public static SquadParticipantRequestResponse from(SquadParticipantRequestDto squadParticipantRequestDto) {
        return new SquadParticipantRequestResponse(
                squadParticipantRequestDto.crewId(),
                squadParticipantRequestDto.crewName(),
                squadParticipantRequestDto.imageUrl(),
                SimpleMemberInfoResponse.from(squadParticipantRequestDto.crewOwner()),
                squadParticipantRequestDto.squads().stream()
                        .map(SquadParticipantResponse::from)
                        .toList()
        );
    }
}

//package revi1337.onsquad.squad_participant.presentation.dto;
//
//import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
//import revi1337.onsquad.squad_participant.application.dto.SquadParticipantRequestDto;
//
//import java.util.List;
//
//public record SquadParticipantRequestResponse(
//        Long crewId,
//        String crewName,
//        SimpleMemberInfoResponse crewOwner,
//        List<SquadParticipantResponse> requests
//) {
//    public static SquadParticipantRequestResponse from(SquadParticipantRequestDto squadParticipantRequestDto) {
//        return new SquadParticipantRequestResponse(
//                squadParticipantRequestDto.crewId(),
//                squadParticipantRequestDto.crewName(),
//                SimpleMemberInfoResponse.from(squadParticipantRequestDto.crewOwner()),
//                squadParticipantRequestDto.requests().stream()
//                        .map(SquadParticipantResponse::from)
//                        .toList()
//        );
//    }
//}
