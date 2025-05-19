package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SquadParticipantRequest(
        Long crewId,
        Name crewName,
        String imageUrl,
        SimpleMemberDomainDto crewOwner,
        List<SquadParticipantDomainDto> squads
) {
    @QueryProjection
    public SquadParticipantRequest(Long crewId, Name crewName, String imageUrl, SimpleMemberDomainDto crewOwner,
                                   List<SquadParticipantDomainDto> squads) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.crewOwner = crewOwner;
        this.squads = squads;
    }
}
