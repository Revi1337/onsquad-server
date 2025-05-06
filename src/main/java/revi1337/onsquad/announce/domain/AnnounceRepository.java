package revi1337.onsquad.announce.domain;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.NOT_FOUND;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;

public interface AnnounceRepository {

    Announce save(Announce announce);

    Announce saveAndFlush(Announce announce);

    Optional<Announce> findByIdAndCrewId(Long id, Long crewId);

    Page<AnnounceDomainDto> fetchAllByCrewId(Long crewId, Pageable pageable);

    AnnounceDomainDto fetchCacheByCrewIdAndId(Long crewId, Long id);

    default Announce getByIdAndCrewId(Long id, Long crewId) {
        return findByIdAndCrewId(id, crewId)
                .orElseThrow(() -> new AnnounceBusinessException.NotFoundById(NOT_FOUND, id));
    }
}
