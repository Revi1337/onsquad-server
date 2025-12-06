package revi1337.onsquad.squad_comment.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

public interface SquadCommentJpaRepository extends JpaRepository<SquadComment, Long> {

    @Query("select sc from SquadComment sc"
            + " inner join sc.squad as s on sc.id = :id and s.id = :squadId"
            + " inner join s.crew as c on c.id = :crewId")
    Optional<SquadComment> findByIdAndSquadIdAndCrewId(Long id, Long squadId, Long crewId);

    Optional<SquadComment> findByIdAndSquadId(Long id, Long squadId);

    @Query("select sc from SquadComment sc inner join fetch sc.squad as s where sc.id = :id and sc.squad.id = :squadId")
    Optional<SquadComment> findWithSquadByIdAndSquadId(Long id, Long squadId);

}
