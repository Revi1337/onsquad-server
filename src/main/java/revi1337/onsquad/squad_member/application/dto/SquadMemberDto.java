package revi1337.onsquad.squad_member.application.dto;

import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;

import java.time.LocalDateTime;

public record SquadMemberDto(
        SimpleMemberInfoDto memberInfo,
        LocalDateTime participantAt
) {
    public static SquadMemberDto from(SquadMemberDomainDto squadMemberDomainDto) {
        return new SquadMemberDto(
                SimpleMemberInfoDto.from(squadMemberDomainDto.memberInfo()),
                squadMemberDomainDto.participantAt()
        );
    }
}




//package revi1337.onsquad.squad_member.application.dto;
//
//import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
//import revi1337.onsquad.squad_member.domain.dto.SquadMemberDomainDto;
//
//import java.time.LocalDateTime;
//
//public record SquadMemberDto(
//        Long squadId,
//        String squadTitle,
//        SimpleMemberInfoDto memberInfo,
//        LocalDateTime participantAt
//) {
//    public static SquadMemberDto from(SquadMemberDomainDto squadMemberDomainDto) {
//        return new SquadMemberDto(
//                squadMemberDomainDto.squadId(),
//                squadMemberDomainDto.squadTitle().getValue(),
//                SimpleMemberInfoDto.from(squadMemberDomainDto.memberInfo()),
//                squadMemberDomainDto.participantAt()
//        );
//    }
//}
