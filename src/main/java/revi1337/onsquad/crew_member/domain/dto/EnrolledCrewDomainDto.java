package revi1337.onsquad.crew_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record EnrolledCrewDomainDto(
        Long id,
        Name name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberInfoDomainDto owner
) {
    @QueryProjection
    public EnrolledCrewDomainDto(
            Long id,
            Name name,
            String imageUrl,
            boolean isOwner,
            SimpleMemberInfoDomainDto owner
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isOwner = isOwner;
        this.owner = owner;
    }
}
