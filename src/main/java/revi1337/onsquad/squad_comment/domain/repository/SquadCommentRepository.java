package revi1337.onsquad.squad_comment.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

public interface SquadCommentRepository {

    SquadComment save(SquadComment comment);

    Optional<SquadComment> findById(Long id);

    Optional<SquadComment> findWithSquadById(Long id);

    Page<SquadComment> fetchAllParentsBySquadId(Long squadId, Pageable pageable);

    Page<SquadComment> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable);

    int deleteByMemberId(Long memberId);

    int deleteBySquadIdIn(List<Long> squadIds);

}
