package revi1337.onsquad.announce.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnounceJpaRepository extends JpaRepository<Announce, Long> {

    Optional<Announce> findByIdAndCrewId(Long id, Long crewId);

}
