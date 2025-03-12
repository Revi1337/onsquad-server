package revi1337.onsquad.announce.domain;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.NOT_FOUND;

import java.util.List;
import java.util.Optional;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;

public interface AnnounceRepository {

    Long DEFAULT_FETCH_SIZE = 4L;

    Announce save(Announce announce);

    Announce saveAndFlush(Announce announce);

    Optional<Announce> findByIdAndCrewId(Long crewId, Long id);

    List<AnnounceInfoDomainDto> fetchAnnouncesByCrewId(Long crewId);

    List<AnnounceInfoDomainDto> findLimitedAnnouncesByCrewId(Long crewId);

    List<AnnounceInfoDomainDto> fetchCachedLimitedAnnouncesByCrewId(Long crewId);

    AnnounceInfoDomainDto getCachedByCrewIdAndIdAndMemberId(Long crewId, Long announceId, Long memberId);

    default Announce getByCrewIdAndId(Long crewId, Long id) {
        return findByIdAndCrewId(crewId, id)
                .orElseThrow(() -> new AnnounceBusinessException.NotFoundById(NOT_FOUND, id));
    }
}
