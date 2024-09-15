package revi1337.onsquad.announce.domain.dto;

import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;

import java.util.List;

public record AnnounceInfosWithAuthDto(
        boolean hasAuthority,
        List<AnnounceInfoDto> announces
) {
    public AnnounceInfosWithAuthDto(boolean hasAuthority, List<AnnounceInfoDto> announces) {
        this.hasAuthority = hasAuthority;
        this.announces = announces;
    }
}
