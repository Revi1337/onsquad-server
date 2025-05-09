package revi1337.onsquad.squad.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;

public record SquadCreateRequest(
        @NotEmpty String title,
        @NotEmpty String content,
        @Positive int capacity,
        @NotEmpty String address,
        String addressDetail,
        @NotNull List<CategoryType> categories,
        String kakaoLink,
        String discordLink
) {
    public SquadCreateDto toDto() {
        return new SquadCreateDto(title, content, capacity, address, addressDetail, categories, kakaoLink, discordLink);
    }
}
