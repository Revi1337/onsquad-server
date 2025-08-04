package revi1337.onsquad.squad_comment.domain;

import static revi1337.onsquad.squad_comment.error.SquadCommentErrorCode.NOTFOUND_COMMENT;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.squad_comment.domain.dto.SquadCommentDomainDto;
import revi1337.onsquad.squad_comment.error.exception.SquadCommentBusinessException;

public interface SquadCommentRepository {

    SquadComment save(SquadComment comment);

    Optional<SquadComment> findById(Long id);

    Optional<SquadComment> findByIdAndSquadId(Long id, Long squadId);

    Optional<SquadComment> findWithSquadByIdAndSquadId(Long id, Long squadId);

    Optional<SquadComment> findByIdAndSquadIdAndCrewId(Long id, Long squadId, Long crewId);

    Map<Long, SquadCommentDomainDto> fetchAllParentsBySquadId(Long squadId, Pageable pageable);

    List<SquadCommentDomainDto> fetchAllChildrenByParentIdIn(Collection<Long> parentIds, int childSize);

    List<SquadCommentDomainDto> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable);

    List<SquadCommentDomainDto> findAllWithMemberBySquadId(Long squadId);

    default SquadComment getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new SquadCommentBusinessException.NotFound(NOTFOUND_COMMENT));
    }

    default SquadComment getByIdAndSquadId(Long id, Long squadId) {
        return findByIdAndSquadId(id, squadId)
                .orElseThrow(() -> new SquadCommentBusinessException.NotFound(NOTFOUND_COMMENT));
    }

    default SquadComment getWithSquadByIdAndSquadId(Long id, Long squadId) {
        return findWithSquadByIdAndSquadId(id, squadId)
                .orElseThrow(() -> new SquadCommentBusinessException.NotFound(NOTFOUND_COMMENT));
    }
}
