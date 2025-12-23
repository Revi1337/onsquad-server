package revi1337.onsquad.squad_request.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

@Deprecated
public record SquadRequestWithSquadAndCrewResult(
        Long crewId,
        String crewName,
        String imageUrl,
        SimpleMemberDomainDto crewOwner,
        List<SquadRequestWithSquadResult> squads
) {

    @QueryProjection
    public SquadRequestWithSquadAndCrewResult(Long crewId, String crewName, String imageUrl, SimpleMemberDomainDto crewOwner,
                                              List<SquadRequestWithSquadResult> squads) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.crewOwner = crewOwner;
        this.squads = squads;
    }
}
