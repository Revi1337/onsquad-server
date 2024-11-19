package revi1337.onsquad.crew.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

@Getter
public class CrewInfoDomainDto {

    private Long id;
    private Name name;
    private Introduce introduce;
    private Detail detail;
    private String imageUrl;
    private String kakaoLink;
    @Setter
    private Collection<HashtagType> hashtagTypes;
    private Long memberCnt;
    private SimpleMemberInfoDomainDto crewOwner;

    @QueryProjection
    public CrewInfoDomainDto(Long id, Name name, Introduce introduce, Detail detail, String imageUrl, String kakaoLink,
                             Collection<HashtagType> hashtagTypes, Long memberCnt,
                             SimpleMemberInfoDomainDto crewOwner) {
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

    @QueryProjection
    public CrewInfoDomainDto(Long id, Name name, Introduce introduce, String imageUrl, String kakaoLink,
                             Collection<HashtagType> hashtagTypes, Long memberCnt,
                             SimpleMemberInfoDomainDto crewOwner) {
        this(id, name, introduce, null, imageUrl, kakaoLink, hashtagTypes, memberCnt, crewOwner);
    }

    @QueryProjection
    public CrewInfoDomainDto(Long id, Name name, Introduce introduce, String imageUrl, String kakaoLink, Long memberCnt,
                             SimpleMemberInfoDomainDto crewOwner) {
        this(id, name, introduce, null, imageUrl, kakaoLink, new ArrayList<>(), memberCnt, crewOwner);
    }
}
