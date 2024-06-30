package revi1337.onsquad.crew.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.vo.Nickname;

public record OwnedCrewsDto(
        Name crewName,
        Detail crewDetail,
        HashTags hashTags,
        Nickname crewOwner
) {
    @QueryProjection
    public OwnedCrewsDto(Name crewName, Detail crewDetail, HashTags hashTags, Nickname crewOwner) {
        this.crewName = crewName;
        this.crewDetail = crewDetail;
        this.hashTags = hashTags;
        this.crewOwner = crewOwner;
    }
}
