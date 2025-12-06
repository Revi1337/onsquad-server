package revi1337.onsquad.announce.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.announce.domain.entity.Announce;

public interface AnnounceJpaRepository extends JpaRepository<Announce, Long> {

    Optional<Announce> findByIdAndCrewId(Long id, Long crewId);

}
