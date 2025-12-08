package revi1337.onsquad.squad_request.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SquadRequestWithSquadAndCrewDomainDto(
        Long crewId,
        Name crewName,
        String imageUrl,
        SimpleMemberDomainDto crewOwner,
        List<SquadRequestWithSquadDomainDto> squads
) {

    @QueryProjection
    public SquadRequestWithSquadAndCrewDomainDto(Long crewId, Name crewName, String imageUrl, SimpleMemberDomainDto crewOwner,
                                                 List<SquadRequestWithSquadDomainDto> squads) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.crewOwner = crewOwner;
        this.squads = squads;
    }
}
