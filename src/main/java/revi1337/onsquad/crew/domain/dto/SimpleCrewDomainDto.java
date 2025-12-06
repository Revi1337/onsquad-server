package revi1337.onsquad.crew.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.crew.domain.entity.vo.Introduce;
import revi1337.onsquad.crew.domain.entity.vo.Name;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SimpleCrewDomainDto(
        Long id,
        Name name,
        Introduce introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberDomainDto owner
) {
    @QueryProjection
    public SimpleCrewDomainDto(Long id, Name name, Introduce introduce, String kakaoLink, String imageUrl,
                               SimpleMemberDomainDto owner) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.kakaoLink = kakaoLink;
        this.imageUrl = imageUrl;
        this.owner = owner;
    }

    @QueryProjection
    public SimpleCrewDomainDto(Long id, Name name, String kakaoLink, String imageUrl,
                               SimpleMemberDomainDto owner) {
        this(id, name, null, kakaoLink, imageUrl, owner);
    }
}
