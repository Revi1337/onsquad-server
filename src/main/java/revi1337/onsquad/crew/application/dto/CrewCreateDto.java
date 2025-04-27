package revi1337.onsquad.crew.application.dto;

import java.util.List;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

public record CrewCreateDto(
        String name,
        String introduce,
        String detail,
        List<HashtagType> hashtags,
        String kakaoLink
) {
}
