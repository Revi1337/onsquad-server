package revi1337.onsquad.squad_comment.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;

public interface SquadCommentRepository {

    SquadComment save(SquadComment comment);

    Optional<SquadComment> findById(Long id);

    List<SquadCommentDomainDto> fetchAllParentsBySquadId(Long squadId, Pageable pageable);

    List<SquadCommentDomainDto> fetchAllChildrenByParentIdIn(Collection<Long> parentIds, int childSize);

    List<SquadCommentDomainDto> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable);

}
