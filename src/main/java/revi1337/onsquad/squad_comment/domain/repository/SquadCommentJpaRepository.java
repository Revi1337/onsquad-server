package revi1337.onsquad.squad_comment.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

public interface SquadCommentJpaRepository extends JpaRepository<SquadComment, Long> {

    @EntityGraph(attributePaths = "squad")
    Optional<SquadComment> findWithSquadById(Long id);

}
