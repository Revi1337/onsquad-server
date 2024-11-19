package revi1337.onsquad.crew_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record EnrolledCrewDomainDto(
        Long crewId,
        Name crewName,
        String imageUrl,
        boolean isOwner,
        SimpleMemberInfoDomainDto crewOwner
) {
    @QueryProjection
    public EnrolledCrewDomainDto(
            Long crewId,
            Name crewName,
            String imageUrl,
            boolean isOwner,
            SimpleMemberInfoDomainDto crewOwner
    ) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.imageUrl = imageUrl;
        this.isOwner = isOwner;
        this.crewOwner = crewOwner;
    }
}
