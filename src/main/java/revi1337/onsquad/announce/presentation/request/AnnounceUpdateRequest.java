package revi1337.onsquad.announce.presentation.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.announce.application.dto.AnnounceUpdateDto;

public record AnnounceUpdateRequest(
        @NotEmpty String title,
        @NotEmpty String content
) {

    public AnnounceUpdateDto toDto() {
        return new AnnounceUpdateDto(title, content);
    }
}
