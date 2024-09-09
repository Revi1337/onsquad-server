package revi1337.onsquad.squad.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;

import java.util.List;

public record SquadCreateRequest(
        @NotEmpty String crewName,
        @NotEmpty String title,
        @NotEmpty String content,
        @Positive int capacity,
        @NotEmpty String address,
        String addressDetail,
        @NotEmpty List<String> categories,
        String kakaoLink,
        String discordLink
) {
    public SquadCreateDto toDto() {
        return new SquadCreateDto(crewName, title, content, capacity, address, addressDetail, categories, kakaoLink, discordLink);
    }
}
