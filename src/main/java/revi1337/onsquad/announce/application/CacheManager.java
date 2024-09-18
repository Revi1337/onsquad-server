package revi1337.onsquad.announce.application;

import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;

import java.util.List;

public interface CacheManager {

    void cacheSpecificCrewAnnounceInfos(Long crewId, List<AnnounceInfoDto> announceInfos);

    List<AnnounceInfoDto> getCachedCrewAnnounceInfosById(Long crewId);

}
