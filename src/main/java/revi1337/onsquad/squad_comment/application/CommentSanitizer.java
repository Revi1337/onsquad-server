package revi1337.onsquad.squad_comment.application;

import java.util.List;
import revi1337.onsquad.squad_comment.application.policy.DefaultCommentMaskPolicy;
import revi1337.onsquad.squad_comment.application.strategy.StackBasedDfsCommentSanitizer;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

@Deprecated
public class CommentSanitizer {

    private final CommentMaskPolicy maskPolicy = new DefaultCommentMaskPolicy();
    private final CommentSanitizeStrategy sanitizeStrategy = new StackBasedDfsCommentSanitizer(maskPolicy);

    public List<SquadCommentResult> sanitize(List<SquadCommentResult> comments) {
        return sanitizeStrategy.sanitize(comments);
    }
}
