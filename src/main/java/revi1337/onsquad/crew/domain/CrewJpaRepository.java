package revi1337.onsquad.crew.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.crew.domain.vo.Name;

public interface CrewJpaRepository extends JpaRepository<Crew, Long>, CrewQueryRepository {

    boolean existsByName(Name name);

    Optional<Crew> findByName(Name name);

}
