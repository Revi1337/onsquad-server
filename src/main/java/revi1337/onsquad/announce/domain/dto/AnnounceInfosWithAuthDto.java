package revi1337.onsquad.announce.domain.dto;

import java.util.List;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;

public record AnnounceInfosWithAuthDto(
        boolean hasAuthority,
        List<AnnounceInfoDto> announces
) {
    public AnnounceInfosWithAuthDto(boolean hasAuthority, List<AnnounceInfoDto> announces) {
        this.hasAuthority = hasAuthority;
        this.announces = announces;
    }
}
