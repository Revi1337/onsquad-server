package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;

public record CrewAndSquadDomainDto(
        Long crewId,
        Name crewName,
        SimpleMemberInfoDomainDto crewOwner,
        SquadDomainDto squadInfo
) {
    @QueryProjection
    public CrewAndSquadDomainDto(Long crewId, Name crewName, SimpleMemberInfoDomainDto crewOwner,
                                 SquadDomainDto squadInfo) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.crewOwner = crewOwner;
        this.squadInfo = squadInfo;
    }
}
