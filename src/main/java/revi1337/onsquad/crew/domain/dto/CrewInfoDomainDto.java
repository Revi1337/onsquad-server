package revi1337.onsquad.crew.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.dto.SimpleMemberInfoDomainDto;

public record CrewInfoDomainDto(
        Long id,
        Name name,
        Introduce introduce,
        Detail detail,
        String imageUrl,
        String kakaoLink,
        HashTags hashTags,
        Long memberCnt,
        SimpleMemberInfoDomainDto crewOwner
) {
    @QueryProjection
    public CrewInfoDomainDto(Long id, Name name, Introduce introduce, String imageUrl, String kakaoLink, HashTags hashTags, Long memberCnt, SimpleMemberInfoDomainDto crewOwner) {
        this(id, name, introduce, null, imageUrl, kakaoLink, hashTags, memberCnt, crewOwner);
    }

    @QueryProjection
    public CrewInfoDomainDto(Long id, Name name, Introduce introduce, Detail detail, String imageUrl, String kakaoLink, HashTags hashTags, Long memberCnt, SimpleMemberInfoDomainDto crewOwner) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.kakaoLink = kakaoLink;
        this.hashTags = hashTags;
        this.memberCnt = memberCnt;
        this.crewOwner = crewOwner;
    }
}
