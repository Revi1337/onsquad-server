package revi1337.onsquad.announce.domain;

import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.*;

public interface AnnounceRepository {

    Long DEFAULT_FETCH_SIZE = 4L;

    Announce save(Announce announce);

    Announce saveAndFlush(Announce announce);

    Optional<Announce> findByIdAndCrewId(Long crewId, Long id);

    Optional<AnnounceInfoDomainDto> findAnnounceByCrewIdAndId(Long crewId, Long id, Long memberId);

    List<AnnounceInfoDomainDto> findAnnouncesByCrewId(Long crewId);

    List<AnnounceInfoDomainDto> findLimitedAnnouncesByCrewId(Long crewId);

    default AnnounceInfoDomainDto getAnnounceByCrewIdAndId(Long crewId, Long id, Long memberId) {
        return findAnnounceByCrewIdAndId(crewId, id, memberId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFoundById(NOT_FOUND, id));
    }

    default Announce getByIdAndCrewId(Long crewId, Long id) {
        return findByIdAndCrewId(crewId, id)
                .orElseThrow(() -> new AnnounceBusinessException.NotFoundById(NOT_FOUND, id));
    }
}
