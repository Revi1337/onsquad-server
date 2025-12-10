package revi1337.onsquad.announce.domain.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.announce.domain.entity.Announce;

public interface AnnounceRepository {

    Announce save(Announce announce);

    Announce saveAndFlush(Announce announce);

    void delete(Announce announce);

    Optional<Announce> findById(Long id);

    Page<AnnounceDomainDto> fetchAllByCrewId(Long crewId, Pageable pageable);

    AnnounceDomainDto fetchCacheByCrewIdAndId(Long crewId, Long id);

}
