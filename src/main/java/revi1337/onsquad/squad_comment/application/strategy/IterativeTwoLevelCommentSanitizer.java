package revi1337.onsquad.squad_comment.application.strategy;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_comment.application.CommentMaskPolicy;
import revi1337.onsquad.squad_comment.application.CommentSanitizeStrategy;
import revi1337.onsquad.squad_comment.domain.result.SquadCommentResult;

/**
 * Implements the {@link CommentSanitizeStrategy} using a simple <b>iterative approach</b> (loop-based).
 * <p>
 * This strategy is optimized for comment structures with a <b>limited depth</b>.
 * <p>
 * <b>Current Limitation:</b> This implementation explicitly supports only <b>two levels of depth (Parent -> Child)</b>. Replies attached to a child comment
 * (Grandchildren) will be included in the output but will <b>not</b> have the {@link CommentMaskPolicy} applied to them, potentially leading to data visibility
 * issues.
 * <p>
 * <b>Future Considerations:</b> For comment structures exceeding two levels, consider using the {@code RecursiveCommentSanitizer} or
 * {@code StackBasedDfsCommentSanitizer} which are designed for arbitrary tree depth. This implementation may require significant refactoring to support deeper
 * nesting levels iteratively.
 */
@RequiredArgsConstructor
@Component
public class IterativeTwoLevelCommentSanitizer implements CommentSanitizeStrategy {

    private final CommentMaskPolicy maskPolicy;

    @Override
    public List<SquadCommentResult> sanitize(List<SquadCommentResult> comments) {
        List<SquadCommentResult> sanitized = new ArrayList<>();
        for (SquadCommentResult parent : comments) {
            SquadCommentResult recreatedParent = maskPolicy.apply(parent);
            List<SquadCommentResult> sanitizedChildren = sanitizeChildren(parent.replies());
            recreatedParent.addReplies(sanitizedChildren);
            sanitized.add(recreatedParent);
        }
        return sanitized;
    }

    private List<SquadCommentResult> sanitizeChildren(List<SquadCommentResult> children) {
        return children.stream()
                .map(maskPolicy::apply)
                .toList();
    }
}
