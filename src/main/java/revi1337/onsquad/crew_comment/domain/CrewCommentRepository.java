package revi1337.onsquad.crew_comment.domain;

import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.NOTFOUND_COMMENT;

public interface CrewCommentRepository {

    CrewComment save(CrewComment crewComment);

    Optional<CrewComment> findById(Long id);

    List<CrewComment> findCommentsWithMemberByCrewId(Long crewId);

    List<CrewComment> findLimitedParentCommentsByCrewId(Long crewId, Pageable pageable);

    List<CrewComment> findLimitedChildCommentsByParentIdIn(List<Long> parentIds, Integer childrenSize);

    default CrewComment getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CrewCommentBusinessException.NotFoundById(NOTFOUND_COMMENT, id));
    }
}
