package revi1337.onsquad.announce.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;

public record AnnounceCreateRequest(
        @NotEmpty String title,
        @NotEmpty String content
) {
    public AnnounceCreateDto toDto() {
        return new AnnounceCreateDto(title, content);
    }
}
