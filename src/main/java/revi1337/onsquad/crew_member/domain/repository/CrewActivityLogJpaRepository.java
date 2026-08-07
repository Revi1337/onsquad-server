package revi1337.onsquad.crew_member.domain.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import revi1337.onsquad.crew_member.domain.entity.CrewActivityLog;

public interface CrewActivityLogJpaRepository extends JpaRepository<CrewActivityLog, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from CrewActivityLog c where c.createdAt between :from and :to")
    void deleteByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
