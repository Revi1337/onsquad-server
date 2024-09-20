package revi1337.onsquad.crew_comment.domain;

import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_comment.domain.dto.CrewCommentDomainDto;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.crew_comment.error.CrewCommentErrorCode.NOTFOUND_COMMENT;

public interface CrewCommentRepository {

    CrewComment save(CrewComment crewComment);

    Optional<CrewComment> findById(Long id);

    List<CrewCommentDomainDto> findAllWithMemberByCrewId(Long crewId);

    List<CrewCommentDomainDto> findLimitedCommentsBothOfParentsAndChildren(Long crewId, Pageable pageable, Integer childSize);

    List<CrewCommentDomainDto> findChildComments(Long crewId, Long parentId, Pageable pageable);

    default CrewComment getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CrewCommentBusinessException.NotFoundById(NOTFOUND_COMMENT, id));
    }
}
