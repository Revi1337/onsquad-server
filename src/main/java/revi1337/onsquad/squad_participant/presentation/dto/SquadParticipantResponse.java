package revi1337.onsquad.squad_participant.presentation.dto;

import revi1337.onsquad.member.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_category.presentation.dto.SquadCategoryResponse;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantDto;

import java.time.LocalDateTime;
import java.util.List;

public record SquadParticipantResponse(
        Long id,
        String title,
        int capacity,
        int remain,
        String address,
        String addressDetail,
        String kakaoLink,
        String discordLink,
        List<SquadCategoryResponse> categories,
        SimpleMemberInfoResponse squadOwner,
        RequestParticipantResponse request
) {
    public static SquadParticipantResponse from(SquadParticipantDto squadParticipantDto) {
        return new SquadParticipantResponse(
                squadParticipantDto.id(),
                squadParticipantDto.title(),
                squadParticipantDto.capacity(),
                squadParticipantDto.remain(),
                squadParticipantDto.address(),
                squadParticipantDto.addressDetail(),
                squadParticipantDto.kakaoLink(),
                squadParticipantDto.discordLink(),
                squadParticipantDto.categories().stream()
                        .map(SquadCategoryResponse::from)
                        .toList(),
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








//package revi1337.onsquad.squad_participant.presentation.dto;
//
//import revi1337.onsquad.squad.presentation.dto.response.SquadInfoResponse;
//import revi1337.onsquad.squad_participant.application.dto.SquadParticipantDto;
//
//import java.time.LocalDateTime;
//
//public record SquadParticipantResponse(
//        Long id,
//        LocalDateTime requestAt,
//        SquadInfoResponse squadInfo
//) {
//    public static SquadParticipantResponse from(SquadParticipantDto squadParticipantDto) {
//        return new SquadParticipantResponse(
//                squadParticipantDto.id(),
//                squadParticipantDto.requestAt(),
//                SquadInfoResponse.from(squadParticipantDto.squadInfo())
//        );
//    }
//}
