package revi1337.onsquad.announce.application.dto;

import java.util.List;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;

public record AnnouncesWithCreateStateDto(
        boolean canCreate,
        List<AnnounceDto> announces
) {
    public static AnnouncesWithCreateStateDto from(Boolean canCreate, List<AnnounceDomainDto> announceDtos) {
        return new AnnouncesWithCreateStateDto(
                canCreate,
                announceDtos.stream()
                        .map(AnnounceDto::from)
                        .toList()
        );

    }
}
