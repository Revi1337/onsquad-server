package revi1337.onsquad.squad_comment.application.policy;

import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@Deprecated
public class DefaultCommentMaskPolicy implements CommentMaskPolicy {

    @Override
    public SquadCommentResult apply(SquadCommentResult comment) {
        if (comment.deleted()) {
            return mask(comment);
        }
        return unmask(comment);
    }

    private SquadCommentResult unmask(SquadCommentResult c) {
        return new SquadCommentResult(
                c.parentId(),
                c.id(),
                c.content(),
                c.deleted(),
                c.createdAt(),
                c.updatedAt(),
                c.writer()
        );
    }

    private SquadCommentResult mask(SquadCommentResult c) {
        return new SquadCommentResult(
                c.parentId(),
                c.id(),
                "",
                c.deleted(),
                c.createdAt(),
                c.updatedAt(),
                null
        );
    }
}
