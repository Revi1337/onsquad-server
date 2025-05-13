package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record SquadParticipantRequest(
        Long crewId,
        Name crewName,
        String imageUrl,
        SimpleMemberInfoDomainDto crewOwner,
        List<SquadParticipantDomainDto> squads
) {
    @QueryProjection
    public SquadParticipantRequest(Long crewId, Name crewName, String imageUrl, SimpleMemberInfoDomainDto crewOwner,
                                   List<SquadParticipantDomainDto> squads) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.crewOwner = crewOwner;
        this.squads = squads;
    }
}
