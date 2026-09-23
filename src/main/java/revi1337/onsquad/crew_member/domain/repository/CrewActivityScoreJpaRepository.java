package revi1337.onsquad.crew_member.domain.repository;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityScore;

public interface CrewActivityScoreJpaRepository extends JpaRepository<CrewActivityScore, Long> {

    @Transactional(propagation = REQUIRES_NEW)
    CrewActivityScore save(CrewActivityScore crewActivityScore);

}
