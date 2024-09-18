package revi1337.onsquad.announce.application.event;

import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;

import java.util.List;

public record AnnounceCacheEvent(
        Long crewId,
        List<AnnounceInfoDto> announceInfos
) {
    public String getEventName() {
        return this.getClass().getSimpleName();
    }
}
