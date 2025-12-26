package revi1337.onsquad.squad_comment.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;
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

    public List<SquadCommentResult> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        return squadCommentRepository.fetchAllParentsBySquadId(squadId, pageable);
    }

    public List<SquadCommentResult> fetchAllChildrenByParentIdIn(List<Long> parentIds, int childSize) {
        return squadCommentRepository.fetchAllChildrenByParentIdIn(parentIds, childSize);
    }

    public List<SquadCommentResult> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        return squadCommentRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable);
    }
}
