package revi1337.onsquad.crew.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record EnrolledCrewResult(
        Long id,
        Name name,
        String imageUrl,
        boolean isOwner,
        SimpleMemberDomainDto owner
) {

    @QueryProjection
    public EnrolledCrewResult(
            Long id,
            Name name,
            String imageUrl,
            boolean isOwner,
            SimpleMemberDomainDto owner
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isOwner = isOwner;
        this.owner = owner;
    }
}
