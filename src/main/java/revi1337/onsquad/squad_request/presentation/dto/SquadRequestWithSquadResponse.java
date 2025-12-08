package revi1337.onsquad.squad_request.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;
import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
import revi1337.onsquad.squad_request.application.dto.SquadRequestWithSquadDto;

public record SquadRequestWithSquadResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        List<String> categories,
        SimpleMemberResponse owner,
        RequestParticipantResponse request
) {

    public static SquadRequestWithSquadResponse from(SquadRequestWithSquadDto squadRequestWithSquadDto) {
        return new SquadRequestWithSquadResponse(
                squadRequestWithSquadDto.id(),
                squadRequestWithSquadDto.title(),
                squadRequestWithSquadDto.capacity(),
                squadRequestWithSquadDto.remain(),
                squadRequestWithSquadDto.categories(),
                SimpleMemberResponse.from(squadRequestWithSquadDto.squadOwner()),
                RequestParticipantResponse.from(squadRequestWithSquadDto.request())
        );
    }

    public record RequestParticipantResponse(
            Long id,
            LocalDateTime requestAt
    ) {

        public static RequestParticipantResponse from(SquadRequestWithSquadDto.RequestParticipantDto requestParticipantDto) {
            return new RequestParticipantResponse(
                    requestParticipantDto.id(),
                    requestParticipantDto.requestAt()
            );
        }
    }
}

//package revi1337.onsquad.squad_request.presentation.dto;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import revi1337.onsquad.member.presentation.dto.response.SimpleMemberResponse;
//import revi1337.onsquad.squad_request.application.dto.SquadParticipantDto;
//
//public record SquadParticipantResponse(
//        Long id,
//        String title,
//        int capacity,
//        int remain,
//        List<String> categories,
//        SimpleMemberResponse owner,
//        RequestParticipantResponse request
//) {
//
//    public static SquadParticipantResponse from(SquadParticipantDto squadParticipantDto) {
//        return new SquadParticipantResponse(
//                squadParticipantDto.id(),
//                squadParticipantDto.title(),
//                squadParticipantDto.capacity(),
//                squadParticipantDto.remain(),
//                squadParticipantDto.categories(),
//                SimpleMemberResponse.from(squadParticipantDto.squadOwner()),
//                RequestParticipantResponse.from(squadParticipantDto.request())
//        );
//    }
//
//    public record RequestParticipantResponse(
//            Long id,
//            LocalDateTime requestAt
//    ) {
//
//        public static RequestParticipantResponse from(SquadParticipantDto.RequestParticipantDto requestParticipantDto) {
//            return new RequestParticipantResponse(
//                    requestParticipantDto.id(),
//                    requestParticipantDto.requestAt()
//            );
//        }
//    }
//}
