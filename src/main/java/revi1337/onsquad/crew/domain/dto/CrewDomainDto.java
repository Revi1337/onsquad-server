package revi1337.onsquad.crew.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

@Getter
public class CrewDomainDto {

    private Long id;
    private Name name;
    private Introduce introduce;
    private Detail detail;
    private String imageUrl;
    private String kakaoLink;
    @Setter
    private Collection<HashtagType> hashtagTypes;
    private Long memberCnt;
    private SimpleMemberDomainDto crewOwner;

    @QueryProjection
    public CrewDomainDto(Long id, Name name, Introduce introduce, Detail detail, String imageUrl, String kakaoLink,
                         Collection<HashtagType> hashtagTypes, Long memberCnt,
                         SimpleMemberDomainDto crewOwner) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.kakaoLink = kakaoLink;
        this.hashtagTypes = hashtagTypes;
        this.memberCnt = memberCnt;
        this.crewOwner = crewOwner;
    }

    public void addHashtagTypes(List<HashtagType> hashtagTypes) {
        this.hashtagTypes.addAll(hashtagTypes);
    }

    @QueryProjection
    public CrewDomainDto(Long id, Name name, Introduce introduce, String imageUrl, String kakaoLink,
                         Collection<HashtagType> hashtagTypes, Long memberCnt,
                         SimpleMemberDomainDto crewOwner) {
        this(id, name, introduce, null, imageUrl, kakaoLink, hashtagTypes, memberCnt, crewOwner);
    }

    @QueryProjection
    public CrewDomainDto(Long id, Name name, Introduce introduce, String imageUrl, String kakaoLink, Long memberCnt,
                         SimpleMemberDomainDto crewOwner) {
        this(id, name, introduce, null, imageUrl, kakaoLink, new ArrayList<>(), memberCnt, crewOwner);
    }
}
