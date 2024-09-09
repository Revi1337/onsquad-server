package revi1337.onsquad.crew.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.crew.dto.CrewUpdateDto;

import java.util.List;

public record CrewUpdateRequest(
        @NotEmpty String name,
        @NotEmpty String introduce,
        @NotEmpty String detail,
        List<String> hashTags,
        String kakaoLink
) {
    public CrewUpdateDto toDto() {
        return new CrewUpdateDto(name, introduce, detail, hashTags, kakaoLink);
    }
}
