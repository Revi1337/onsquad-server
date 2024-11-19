package revi1337.onsquad.crew.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;

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
