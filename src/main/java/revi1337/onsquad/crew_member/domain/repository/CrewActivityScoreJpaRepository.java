package revi1337.onsquad.crew_member.domain.repository;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityScore;

public interface CrewActivityScoreJpaRepository extends JpaRepository<CrewActivityScore, Long> {

    @Transactional(propagation = REQUIRES_NEW)
    CrewActivityScore save(CrewActivityScore crewActivityScore);

    @Modifying(clearAutomatically = true)
    @Query("delete from CrewActivityScore c where c.lastActivityAt between :from and :to")
    void deleteByLastActivityAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
