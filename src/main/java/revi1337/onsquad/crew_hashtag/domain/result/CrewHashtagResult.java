package revi1337.onsquad.crew_hashtag.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

public record CrewHashtagResult(
        Long id,
        HashtagType hashTagType
) {

    @QueryProjection
    public CrewHashtagResult(Long id, HashtagType hashTagType) {
        this.id = id;
        this.hashTagType = hashTagType;
    }

    @QueryProjection
    public CrewHashtagResult(HashtagType hashtagType) {
        this(null, hashtagType);
    }
}
