package revi1337.onsquad.squad_comment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentRepository;
import revi1337.onsquad.squad_comment.error.SquadCommentBusinessException;
import revi1337.onsquad.squad_comment.error.SquadCommentErrorCode;

@RequiredArgsConstructor
@Component
public class SquadCommentAccessPolicy {

    private final SquadCommentRepository squadCommentRepository;

    public SquadComment ensureCommentExistsAndGet(Long commentId) {
        return squadCommentRepository.findById(commentId)
                .orElseThrow(() -> new SquadCommentBusinessException.NotFound(SquadCommentErrorCode.NOTFOUND_COMMENT));
    }

    public void ensureMatchSquad(SquadComment comment, Long squadId) {
        if (comment.mismatchSquadId(squadId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
    }

    public void ensureCommentIsParent(SquadComment comment) {
        if (comment.isNotParent()) {
            throw new SquadCommentBusinessException.NotParent(SquadCommentErrorCode.NOT_PARENT);
        }
    }

    public void ensureCommentIsAlive(SquadComment comment) {
        if (comment.isDeleted()) {
            throw new SquadCommentBusinessException.Deleted(SquadCommentErrorCode.DELETED);
        }
    }

    public void ensureMatchWriter(SquadComment comment, Long memberId) {
        if (comment.mismatchMemberId(memberId)) {
            throw new SquadCommentBusinessException.InsufficientAuthority(SquadCommentErrorCode.MISMATCH_WRITER);
        }
    }
}
