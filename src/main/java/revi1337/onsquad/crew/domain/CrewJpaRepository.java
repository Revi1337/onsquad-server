package revi1337.onsquad.crew.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.crew.domain.vo.Name;

public interface CrewJpaRepository extends JpaRepository<Crew, Long> {

    boolean existsByName(Name name);

}
