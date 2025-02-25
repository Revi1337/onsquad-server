package revi1337.onsquad.crew.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

public record CrewCreateRequest(
        @NotEmpty String name,
        @NotEmpty String introduce,
        @NotEmpty String detail,
        @NotNull List<HashtagType> hashtags,
        String kakaoLink
) {
    public CrewCreateDto toDto() {
        return CrewCreateDto.of(name, introduce, detail, hashtags, kakaoLink);
    }
}
