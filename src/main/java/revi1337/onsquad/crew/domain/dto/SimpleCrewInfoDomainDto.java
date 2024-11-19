package revi1337.onsquad.crew.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record SimpleCrewInfoDomainDto(
        Long id,
        Name name,
        Introduce introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberInfoDomainDto owner
) {
    @QueryProjection
    public SimpleCrewInfoDomainDto(Long id, Name name, Introduce introduce, String kakaoLink, String imageUrl,
                                   SimpleMemberInfoDomainDto owner) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.kakaoLink = kakaoLink;
        this.imageUrl = imageUrl;
        this.owner = owner;
    }

    @QueryProjection
    public SimpleCrewInfoDomainDto(Long id, Name name, String kakaoLink, String imageUrl,
                                   SimpleMemberInfoDomainDto owner) {
        this(id, name, null, kakaoLink, imageUrl, owner);
    }
}
