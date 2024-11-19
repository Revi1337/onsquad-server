package revi1337.onsquad.announce.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnounceJpaRepository extends JpaRepository<Announce, Long> {

    Optional<Announce> findByIdAndCrewId(Long id, Long crewId);

}
