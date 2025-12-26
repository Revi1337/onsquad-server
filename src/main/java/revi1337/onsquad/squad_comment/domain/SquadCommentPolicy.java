package revi1337.onsquad.squad_comment.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.error.SquadCommentBusinessException;
import revi1337.onsquad.squad_comment.error.SquadCommentErrorCode;

@RequiredArgsConstructor
public class SquadCommentPolicy {

    public static void ensureMatchSquad(SquadComment comment, Long squadId) {
        if (comment.mismatchSquadId(squadId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
    }

    public static void ensureCommentIsParent(SquadComment comment) {
        if (comment.isNotParent()) {
            throw new SquadCommentBusinessException.NotParent(SquadCommentErrorCode.NOT_PARENT);
        }
    }

    public static void ensureCommentIsAlive(SquadComment comment) {
        if (comment.isDeleted()) {
            throw new SquadCommentBusinessException.Deleted(SquadCommentErrorCode.DELETED);
        }
    }

    public static void ensureMatchWriter(SquadComment comment, Long memberId) {
        if (comment.mismatchMemberId(memberId)) {
            throw new SquadCommentBusinessException.InsufficientAuthority(SquadCommentErrorCode.MISMATCH_WRITER);
        }
    }
}
