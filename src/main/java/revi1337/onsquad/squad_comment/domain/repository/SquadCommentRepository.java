package revi1337.onsquad.squad_comment.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

public interface SquadCommentRepository {

    SquadComment save(SquadComment comment);

    Optional<SquadComment> findById(Long id);

    Optional<SquadComment> findWithSquadById(Long id);

    List<SquadCommentResult> fetchAllParentsBySquadId(Long squadId, Pageable pageable);

    List<SquadCommentResult> fetchAllChildrenByParentIdIn(Collection<Long> parentIds, int childSize);

    List<SquadCommentResult> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable);

}
