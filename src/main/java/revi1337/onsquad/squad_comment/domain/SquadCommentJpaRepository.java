package revi1337.onsquad.squad_comment.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SquadCommentJpaRepository extends JpaRepository<SquadComment, Long> {

    @Query("select sc from SquadComment sc"
            + " inner join sc.squad as s on sc.id = :id and s.id = :squadId"
            + " inner join s.crew as c on c.id = :crewId")
    Optional<SquadComment> findByIdAndSquadIdAndCrewId(Long id, Long squadId, Long crewId);

}
