package revi1337.onsquad.squad_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_comment.error.SquadCommentBusinessException;
import revi1337.onsquad.squad_comment.error.SquadCommentErrorCode;

@RequiredArgsConstructor
@Component
public class SquadCommentAccessor {

    private final SquadCommentRepository squadCommentRepository;

    public SquadComment getById(Long commentId) {
        return squadCommentRepository.findById(commentId)
                .orElseThrow(() -> new SquadCommentBusinessException.NotFound(SquadCommentErrorCode.NOTFOUND_COMMENT));
    }

    public SquadComment getWithSquadById(Long commentId) {
        return squadCommentRepository.findWithSquadById(commentId)
                .orElseThrow(() -> new SquadCommentBusinessException.NotFound(SquadCommentErrorCode.NOTFOUND_COMMENT));
    }

    public Page<SquadComment> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        return squadCommentRepository.fetchAllParentsBySquadId(squadId, pageable);
    }

    public Page<SquadComment> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        return squadCommentRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable);
    }
}
