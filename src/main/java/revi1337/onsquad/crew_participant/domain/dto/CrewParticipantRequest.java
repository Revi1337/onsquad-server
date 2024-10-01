package revi1337.onsquad.crew_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record CrewParticipantRequest(
        Long crewId,
        Name crewName,
        String imageUrl,
        SimpleMemberInfoDomainDto crewOwner,
        CrewParticipantDomainDto request
) {
    @QueryProjection
    public CrewParticipantRequest(Long crewId, Name crewName, String imageUrl, SimpleMemberInfoDomainDto crewOwner, CrewParticipantDomainDto request) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.crewOwner = crewOwner;
        this.request = request;
    }
}
