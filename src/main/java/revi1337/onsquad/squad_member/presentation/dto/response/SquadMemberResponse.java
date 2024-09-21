package revi1337.onsquad.squad_member.presentation.dto.response;

import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
import revi1337.onsquad.squad_member.application.dto.SquadMemberDto;

import java.time.LocalDateTime;

public record SquadMemberResponse(
        SimpleMemberInfoResponse memberInfo,
        LocalDateTime participantAt
) {
    public static SquadMemberResponse from(SquadMemberDto squadMemberDto) {
        return new SquadMemberResponse(
                SimpleMemberInfoResponse.from(squadMemberDto.memberInfo()),
                squadMemberDto.participantAt()
        );
    }
}




//package revi1337.onsquad.squad_member.presentation.dto.response;
//
//import revi1337.onsquad.member.presentation.dto.response.SimpleMemberInfoResponse;
//import revi1337.onsquad.squad_member.application.dto.SquadMemberDto;
//
//import java.time.LocalDateTime;
//
//public record SquadMemberResponse(
//        Long squadId,
//        String squadTitle,
//        SimpleMemberInfoResponse memberInfo,
//        LocalDateTime participantAt
//) {
//    public static SquadMemberResponse from(SquadMemberDto squadMemberDto) {
//        return new SquadMemberResponse(
//                squadMemberDto.squadId(),
//                squadMemberDto.squadTitle(),
//                SimpleMemberInfoResponse.from(squadMemberDto.memberInfo()),
//                squadMemberDto.participantAt()
//        );
//    }
//}
