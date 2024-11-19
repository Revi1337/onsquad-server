package revi1337.onsquad.crew.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.Optional;

public interface CrewJpaRepository extends JpaRepository<Crew, Long>, CrewQueryRepository {

    boolean existsByName(Name name);

    Optional<Crew> findByName(Name name);

}
