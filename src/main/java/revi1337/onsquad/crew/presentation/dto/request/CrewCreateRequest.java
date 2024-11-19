package revi1337.onsquad.crew.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;

public record CrewCreateRequest(
        @NotEmpty String name,
        @NotEmpty String introduce,
        @NotEmpty String detail,
        List<String> hashTags,
        String kakaoLink
) {
    public CrewCreateDto toDto() {
        return new CrewCreateDto(name, introduce, detail, hashTags, kakaoLink);
    }
}
