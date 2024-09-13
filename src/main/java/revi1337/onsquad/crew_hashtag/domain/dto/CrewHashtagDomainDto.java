package revi1337.onsquad.crew_hashtag.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

public record CrewHashtagDomainDto(
        Long id,
        HashtagType hashTagType
) {
    @QueryProjection
    public CrewHashtagDomainDto(Long id, HashtagType hashTagType) {
        this.id = id;
        this.hashTagType = hashTagType;
    }

    @QueryProjection
    public CrewHashtagDomainDto(HashtagType hashtagType) {
        this(null, hashtagType);
    }
}
