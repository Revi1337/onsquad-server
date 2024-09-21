package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

import java.util.List;

public record SquadParticipantRequest(
        Long crewId,
        Name crewName,
        String imageUrl,
        SimpleMemberInfoDomainDto crewOwner,
        List<SquadParticipantDomainDto> squads
) {
    @QueryProjection
    public SquadParticipantRequest(Long crewId, Name crewName, String imageUrl, SimpleMemberInfoDomainDto crewOwner, List<SquadParticipantDomainDto> squads) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.crewOwner = crewOwner;
        this.squads = squads;
    }
}





//package revi1337.onsquad.squad_participant.domain.dto;
//
//import com.querydsl.core.annotations.QueryProjection;
//import revi1337.onsquad.crew.domain.vo.Name;
//import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
//
//import java.util.List;
//
//public record SquadParticipantRequest(
//        Long crewId,
//        Name crewName,
//        SimpleMemberInfoDomainDto crewOwner,
//        List<SquadParticipantDomainDto> requests
//) {
//    @QueryProjection
//    public SquadParticipantRequest(Long crewId, Name crewName, SimpleMemberInfoDomainDto crewOwner, List<SquadParticipantDomainDto> requests) {
//        this.crewId = crewId;
//        this.crewName = crewName;
//        this.crewOwner = crewOwner;
//        this.requests = requests;
//    }
//}
