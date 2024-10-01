package revi1337.onsquad.backup.crew.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewTopCacheJpaRepository extends JpaRepository<CrewTopCache, Long> {

    List<CrewTopCache> findAllByCrewId(Long crewId);

}
