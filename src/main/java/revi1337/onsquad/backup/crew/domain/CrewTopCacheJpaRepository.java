package revi1337.onsquad.backup.crew.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewTopCacheJpaRepository extends JpaRepository<CrewTopCache, Long> {

    List<CrewTopCache> findAllByCrewId(Long crewId);

}
