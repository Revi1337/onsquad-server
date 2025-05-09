package revi1337.onsquad.squad.application.dto;

import java.util.List;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.squad.domain.Squad.SquadMetadata;

public record SquadCreateDto(
        String title,
        String content,
        int capacity,
        String address,
        String addressDetail,
        List<CategoryType> categories,
        String kakaoLink,
        String discordLink
) {
    public SquadMetadata toEntityMetadata() {
        return new SquadMetadata(title, content, capacity, address, addressDetail, kakaoLink, discordLink);
    }
}
