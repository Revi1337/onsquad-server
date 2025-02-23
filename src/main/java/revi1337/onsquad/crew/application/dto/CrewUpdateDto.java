package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

public record CrewUpdateDto(
        String name,
        String introduce,
        String detail,
        List<Hashtag> hashtags,
        String kakaoLink
) {
    public static CrewUpdateDto of(
            String name,
            String introduce,
            String detail,
            List<HashtagType> hashtagTypes,
            String kakaoLink
    ) {
        List<Hashtag> hashtags = Hashtag.fromHashtagTypes(hashtagTypes);

        return new CrewUpdateDto(name, introduce, detail, hashtags, kakaoLink);
    }
}
