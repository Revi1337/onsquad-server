package revi1337.onsquad.squad_comment.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.error.SquadCommentBusinessException;
import revi1337.onsquad.squad_comment.error.SquadCommentErrorCode;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@NoArgsConstructor(access = PRIVATE)
public class SquadCommentPolicy {

    public static boolean isDeleted(SquadComment comment) {
        return comment.isDeleted();
    }

    public static boolean isNotParent(SquadComment comment) {
        return comment.isNotParent();
    }

    public static boolean canDeleteComment(SquadMember me, SquadComment comment) {
        if (isDeleted(comment)) {
            return false;
        }
        boolean isWriter = me.getMember().equals(comment.getMember());
        boolean isLeader = SquadMemberPolicy.isLeader(me);

        return isWriter || isLeader;
    }

    public static void ensureCommentIsParent(SquadComment comment) {
        if (isNotParent(comment)) {
            throw new SquadCommentBusinessException.NotParent(SquadCommentErrorCode.NOT_PARENT);
        }
    }

    public static void ensureCommentIsAlive(SquadComment comment) {
        if (isDeleted(comment)) {
            throw new SquadCommentBusinessException.Deleted(SquadCommentErrorCode.DELETED);
        }
    }

    public static void ensureDeletable(SquadMember me, SquadComment comment) {
        if (!canDeleteComment(me, comment)) {
            throw new SquadCommentBusinessException.InsufficientAuthority(SquadCommentErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureMatchSquad(SquadComment comment, Long squadId) {
        if (comment.mismatchSquadId(squadId)) {
            throw new SquadCommentBusinessException.MismatchReference(SquadCommentErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
    }

    public static void ensureMatchWriter(SquadComment comment, Long memberId) {
        if (comment.mismatchMemberId(memberId)) {
            throw new SquadCommentBusinessException.InsufficientAuthority(SquadCommentErrorCode.MISMATCH_WRITER);
        }
    }
}
