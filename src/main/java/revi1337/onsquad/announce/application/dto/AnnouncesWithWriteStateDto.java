package revi1337.onsquad.announce.application.dto;

import java.util.List;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;

public record AnnouncesWithWriteStateDto(
        boolean canWrite,
        List<AnnounceDto> announces
) {
    public static AnnouncesWithWriteStateDto from(Boolean canWrite, List<AnnounceDomainDto> announceDtos) {
        return new AnnouncesWithWriteStateDto(
                canWrite,
                announceDtos.stream()
                        .map(AnnounceDto::from)
                        .toList()
        );

    }
}
