package revi1337.onsquad.crew.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record SimpleCrewResult(
        Long id,
        String name,
        String introduce,
        String kakaoLink,
        String imageUrl,
        SimpleMemberDomainDto owner
) {

    @QueryProjection
    public SimpleCrewResult(Long id, String name, String introduce, String kakaoLink, String imageUrl, SimpleMemberDomainDto owner) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.kakaoLink = kakaoLink;
        this.imageUrl = imageUrl;
        this.owner = owner;
    }

    @QueryProjection
    public SimpleCrewResult(Long id, String name, String kakaoLink, String imageUrl, SimpleMemberDomainDto owner) {
        this(id, name, null, kakaoLink, imageUrl, owner);
    }
}
