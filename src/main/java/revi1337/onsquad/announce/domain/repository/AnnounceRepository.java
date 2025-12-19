package revi1337.onsquad.announce.domain.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.result.AnnounceResult;

public interface AnnounceRepository {

    Announce save(Announce announce);

    Announce saveAndFlush(Announce announce);

    void delete(Announce announce);

    Optional<Announce> findById(Long id);

    Page<AnnounceResult> fetchAllByCrewId(Long crewId, Pageable pageable);

    AnnounceResult fetchCacheByCrewIdAndId(Long crewId, Long id);

}
