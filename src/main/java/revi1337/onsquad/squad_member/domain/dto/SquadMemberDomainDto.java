package revi1337.onsquad.squad_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

import java.time.LocalDateTime;

public record SquadMemberDomainDto(
        SimpleMemberInfoDomainDto memberInfo,
        LocalDateTime participantAt
) {
    @QueryProjection
    public SquadMemberDomainDto(SimpleMemberInfoDomainDto memberInfo, LocalDateTime participantAt) {
        this.memberInfo = memberInfo;
        this.participantAt = participantAt;
    }
}




//package revi1337.onsquad.squad_member.domain.dto;
//
//import com.querydsl.core.annotations.QueryProjection;
//import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
//import revi1337.onsquad.squad.domain.vo.Title;
//
//import java.time.LocalDateTime;
//
//public record SquadMemberDomainDto(
//        Long squadId,
//        Title squadTitle,
//        SimpleMemberInfoDomainDto memberInfo,
//        LocalDateTime participantAt
//) {
//    @QueryProjection
//    public SquadMemberDomainDto(Long squadId, Title squadTitle, SimpleMemberInfoDomainDto memberInfo, LocalDateTime participantAt) {
//        this.squadId = squadId;
//        this.squadTitle = squadTitle;
//        this.memberInfo = memberInfo;
//        this.participantAt = participantAt;
//    }
//}
