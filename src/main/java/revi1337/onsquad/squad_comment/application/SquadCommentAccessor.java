package revi1337.onsquad.squad_comment.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
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

    public List<SquadComment> fetchAllParentsBySquadId(Long squadId, Pageable pageable) {
        return squadCommentRepository.fetchAllParentsBySquadId(squadId, pageable);
    }

    public List<SquadComment> fetchAllChildrenBySquadIdAndParentId(Long squadId, Long parentId, Pageable pageable) {
        return squadCommentRepository.fetchAllChildrenBySquadIdAndParentId(squadId, parentId, pageable);
    }
}
