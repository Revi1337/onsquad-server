package revi1337.onsquad.crew_member.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.dto.SimpleMemberInfoDomainDto;

public record OwnedCrewDomainDto(
        Long crewId,
        Name crewName,
        Detail crewDetail,
        String imageUrl,
        boolean isOwner,
        HashTags hashTags,
        SimpleMemberInfoDomainDto crewOwner
) {
    @QueryProjection
    public OwnedCrewDomainDto(Long crewId, Name crewName, Detail crewDetail, String imageUrl, boolean isOwner, HashTags hashTags, SimpleMemberInfoDomainDto crewOwner) {
        this.crewId = crewId;
        this.crewName = crewName;
        this.crewDetail = crewDetail;
        this.imageUrl = imageUrl;
        this.isOwner = isOwner;
        this.hashTags = hashTags;
        this.crewOwner = crewOwner;
    }
}
