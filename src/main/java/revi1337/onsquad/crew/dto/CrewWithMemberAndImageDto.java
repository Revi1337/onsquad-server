package revi1337.onsquad.crew.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.vo.Nickname;

public record CrewWithMemberAndImageDto(
        Name crewName,
        Detail crewDetail,
        HashTags hashTags,
        Nickname crewOwner,
        byte[] image
) {
    @QueryProjection
    public CrewWithMemberAndImageDto(Name crewName, Detail crewDetail, HashTags hashTags, Nickname crewOwner, byte[] image) {
        this.crewName = crewName;
        this.crewDetail = crewDetail;
        this.hashTags = hashTags;
        this.crewOwner = crewOwner;
        this.image = image;
    }
}
