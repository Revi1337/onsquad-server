package revi1337.onsquad.announce.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;

public record AnnounceCreateRequest(
        @NotNull @Positive Long crewId,
        @NotEmpty String title,
        @NotEmpty String content
) {
    public AnnounceCreateDto toDto() {
        return new AnnounceCreateDto(crewId, title, content);
    }
}
